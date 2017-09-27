package com.rollingdice.deft.android.tab.Voice;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hound.android.sdk.AsyncTextSearch;
import com.hound.android.sdk.TextSearch;
import com.hound.android.sdk.TextSearchListener;
import com.hound.android.sdk.VoiceSearchInfo;
import com.hound.android.sdk.VoiceSearchState;
import com.hound.android.sdk.util.HoundRequestInfoFactory;
import com.hound.core.model.sdk.HoundRequestInfo;
import com.hound.core.model.sdk.HoundResponse;
import com.hound.core.util.Utils;
import com.rollingdice.deft.android.tab.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;

public class TextSearchActivity extends Activity {
    private static final String LOG_TAG = TextSearchActivity.class.getSimpleName();

    private TextView textView;
    private Button button;
    private EditText editText;

    private AsyncTextSearch asyncTextSearch;

    private LocationManager locationManager;

    // private JsonNode lastConversationState;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set to true to turn on LogCat debug output for the Houndify SDK
        asyncTextSearch.setDebug( false );

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_textsearch);

        textView = (TextView)findViewById(R.id.textView);
        button = (Button)findViewById(R.id.button);
        editText = (EditText) findViewById( R.id.editText );
        editText.setText("What is the weather");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (asyncTextSearch == null) {
                    resetUIState();
                    startSearch();
                }
                else {
                    // voice search has already started.
                    if (asyncTextSearch.getState() == VoiceSearchState.STATE_STARTED) {
                        asyncTextSearch.abort();
                    }

                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (asyncTextSearch != null) {
            asyncTextSearch.abort();
        }
    }

    private HoundRequestInfo getHoundRequestInfo() {
        final HoundRequestInfo requestInfo = HoundRequestInfoFactory.getDefault(this);

        Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        requestInfo.setUserId("User ID");
        requestInfo.setRequestId(UUID.randomUUID().toString());
        if ( location != null ) {
            requestInfo.setLatitude(location.getLatitude());
            requestInfo.setLongitude(location.getLongitude());
            requestInfo.setPositionHorizontalAccuracy((double) location.getAccuracy());
        }

        // Note: now handled by the Hound SDK, now necessary manage returning this unless you want to override it.
        // for the first search lastConversationState will be null, this is okay.  However any future
        // searches may return us a conversation state to use.  Add it to the request info when we have one.
        // requestInfo.setConversationState(lastConversationState);

        return requestInfo;
    }

    private void startSearch() {
        if (asyncTextSearch != null) {
            return; // We are already searching
        }

        setProgressBarIndeterminateVisibility(true);

        // Using the AsyncTextSearch is the preferred way to run text searches
        boolean useAsyncTextSearch = true;
        if ( useAsyncTextSearch ) {

            AsyncTextSearch.Builder builder = new AsyncTextSearch.Builder()
                    // Uncomment next line to turn on sending SDK debug info to LogCat
                    .setDebug( true )

                    /** End point used for SoundHound Houndifcation, by default this value is
                     * https://api.houndify.com/v1, this value can be changed by uncommenting
                     * the line below and setting a new URL
                     */
                     // .setEndpoint("https://api.houndify.com/v1")
                    .setRequestInfo(getHoundRequestInfo())
                    .setClientId( Constants.CLIENT_ID )
                    .setClientKey( Constants.CLIENT_KEY )
                    .setListener( textSearchListener )
                    .setQuery( editText.getText().toString() );

            Log.i(LOG_TAG, "Connecting to: " + builder.getEndpoint());

            asyncTextSearch = builder.build();

            textView.setText("Waiting for response...");
            button.setText("Stop Search");

            asyncTextSearch.start();
        }
        else {
            // Synchronous text search call done on separate thread
            new Thread(new Runnable() {
                @Override
                public void run() {
                    TextSearch textSearch = new TextSearch.Builder()
                            .setRequestInfo(getHoundRequestInfo())
                            .setClientId(Constants.CLIENT_ID)
                            .setClientKey(Constants.CLIENT_KEY)
                            //.setListener(textSearchListener)
                            .setQuery(editText.getText().toString()).build();
                    try {
                        TextSearch.Result result = textSearch.search();
                        VoiceSearchInfo voiceSearchInfo = result.getSearchInfo();
                        Log.d( LOG_TAG, voiceSearchInfo.getContentBody() );
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "Failed text search" + Utils.printStack( e ) );

                    }
                }
            }).start();
        }

    }

    private void resetUIState() {
        setProgressBarIndeterminateVisibility(false);
        button.setEnabled(true);
        button.setText("Submit text");
    }

    private final TextSearchListener textSearchListener = new TextSearchListener() {


        @Override
        public void onResponse(final HoundResponse response, final VoiceSearchInfo info) {
            asyncTextSearch = null;
            resetUIState();

            // Make sure the request succeeded with OK
            if ( response.getStatus().equals( HoundResponse.Status.OK ) ) {

            /* Now handled by the HoundSDK
            if (!response.getResults().isEmpty()) {
                // Save off the conversation state.  This information will be returned to the server
                // in the next search. Note that at some point in the future the results CommandResult list
                // may contain more than one item. For now it does not, so just grab the first result's
                // conversation state and use it.
                lastConversationState = response.getResults().get(0).getConversationState();
            }
            */

                textView.setText("Received response...displaying the JSON");

                // We put pretty printing JSON on a separate thread as the server JSON can be quite large and will stutter the UI

                // Not meant to be configuration change proof, this is just a demo
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String message;
                        try {
                            message = "Response\n\n" + new JSONObject(info.getContentBody()).toString(4);
                        } catch (final JSONException ex) {
                            textView.setText("Bad JSON\n\n" + response);
                            message = "Bad JSON\n\n" + response;
                        }

                        final String finalMessage = message;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText(finalMessage);
                            }
                        });
                    }
                }).start();
            }
            else  {
                textView.setText( "Request failed with: " + response.getErrorMessage() );
            }
        }

        @Override
        public void onError(final Exception ex, final VoiceSearchInfo info) {
            asyncTextSearch = null;
            resetUIState();
            textView.setText(exceptionToString(ex));
        }


        @Override
        public void onAbort(final VoiceSearchInfo info) {
            asyncTextSearch = null;
            resetUIState();
            textView.setText("Aborted");
        }
    };

    private static String exceptionToString(final Exception ex) {
        try {
            final StringWriter sw = new StringWriter(1024);
            final PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            pw.close();
            return sw.toString();
        }
        catch (final Exception e) {
            return "";
        }
    }
}
