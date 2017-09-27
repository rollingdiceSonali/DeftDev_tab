package com.rollingdice.deft.android.tab.Voice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.hound.android.fd.HoundSearchResult;
import com.hound.android.fd.Houndify;
import com.hound.android.libphs.PhraseSpotterReader;
import com.hound.android.sdk.VoiceSearch;
import com.hound.android.sdk.VoiceSearchInfo;
import com.hound.android.sdk.audio.SimpleAudioByteStreamSource;
import com.hound.core.model.sdk.CommandResult;
import com.hound.core.model.sdk.HoundResponse;
import com.rollingdice.deft.android.tab.GlobalApplication;
import com.rollingdice.deft.android.tab.R;
import com.rollingdice.deft.android.tab.helperClasses.DisjointIgnoreCase;
import com.rollingdice.deft.android.tab.model.CurtainDetails;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rollingdice.deft.android.tab.model.RoomAppliance;
import com.rollingdice.deft.android.tab.model.RoomDetails;
import com.rollingdice.deft.android.tab.model.modeDetails;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


/**
 * This sample demonstrates how to call the HoundVoiceSearchActivity to run a Hound voice search.
 * This is the preferred and easiest method for integrating Houndify into an application since much of the
 * detailed search UI and state management is automatically taken care of for you.
 */
public class HoundVoiceSearchExampleActivity extends Activity {
    private TextView textView;
    private PhraseSpotterReader phraseSpotterReader;
    private Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    private TextToSpeechMgr textToSpeechMgr;
    private Houndify houndify;

    private static final int REQUEST_CODE = 1234;
    private static final int REQUEST_CODE_FINAL = 9876;
    ImageButton Start;


    String text;

    int position = 0;

    TextToSpeech tts123;
    String customerName = "";
    private HashMap<String, String> myHashMap;
    Collection<String> keywordRoomList = new ArrayList<>();
    Collection<String> keywordStateList = new ArrayList<>();
    Collection<String> keywordLevelList = new ArrayList<>();
    Collection<String> keywordApplianceList = new ArrayList<>();
    Collection<String> keywordMoodList = new ArrayList<>();

    private List<RoomDetails> roomList = new ArrayList<>();

    private List<RoomAppliance> appliances = new ArrayList<>();
    private List<CurtainDetails> curtains = new ArrayList<>();
    private List<modeDetails> moods = new ArrayList<>();


    public DatabaseReference localRef;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        houndify = Houndify.get( this );

        // The activity_hound_search layout contains the com.hound.android.fd.HoundifyButton which is displayed
        // as the black microphone. When press it will load the HoundifyVoiceSearchActivity.
        setContentView( R.layout.activity_hound_search);

        // Text view for displaying written result
        textView = (TextView)findViewById(R.id.textView);

        // Setup TextToSpeech
        textToSpeechMgr = new TextToSpeechMgr( this );

        // Normally you'd only have to do this once in your Application#onCreate
        houndify.setClientId( Constants.CLIENT_ID );
        houndify.setClientKey( Constants.CLIENT_KEY );
        houndify.get(this).setRequestInfoFactory(StatefulRequestInfoFactory.get(this));

        // Turn on debug output. This should be disable for your production code.
        VoiceSearch.setDebug( true );


        houndify.setVoiceSearchBuilderInterceptor(new Houndify.VoiceSearchBuilderInterceptor() {
            @Override
            public void intercept(VoiceSearch.Builder builder) {
                // Example of setting explicit hound server endpoint
                // builder.setEndpoint( "https://api.houndify.com/v1/audio" );

                // Property for specifying the input language, only works for supported languages
                // and defaults to English if not set.
                builder.setInputLanguageIetfTag( Locale.getDefault().getLanguage());
            }
        });

        localRef = GlobalApplication.firebaseRef;

        GetCustomerName();

        GetRoomList();
        GetApplianceList();
        GetMoodList();
        GetCurtainList();
        SetStateKeywords();
    }

    private void SetMoodKeywords()
    {
        try {
            for (int i = 0; i < moods.size(); i++) {
                keywordMoodList.add(moods.get(i).getMoodName());
            }

        } catch (Exception e)
        {
            if(Customer.getCustomer() != null && Customer.getCustomer().customerId != null) {
                DatabaseReference errorRef = localRef.child("notification").child("error")
                        .child(Customer.getCustomer().customerId)
                        .child(String.valueOf(System.currentTimeMillis()));
                String currentStatus = e.getMessage();
                errorRef.setValue(currentStatus);
            }
        }
    }

    private void GetApplianceList() {
        try {
            if(Customer.getCustomer() != null && Customer.getCustomer().customerId != null) {
                DatabaseReference roomDetails = GlobalApplication.firebaseRef.child("rooms")
                        .child(Customer.getCustomer().customerId).child("roomdetails");

                roomDetails.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        appliances = new ArrayList<>();
                        for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
                            //Getting each room
                            for (DataSnapshot type : roomSnapshot.getChildren()) {

                                for(DataSnapshot slave : type.getChildren()) {

                                    if (slave.getRef().toString().indexOf("appliance") != 0 && !(slave.getRef().toString().contains("sensors"))) {
                                        for (DataSnapshot applianc : slave.getChildren()) {
                                            RoomAppliance roomAppliance = applianc.getValue(RoomAppliance.class);
                                            appliances.add(roomAppliance);
                                        }
                                    }
                                }
                            }
                        }
                        SetApplianceKeywords();
                    }

                    @Override
                    public void onCancelled(DatabaseError DatabaseError) {
                        System.out.println("The read failed: " + DatabaseError.getMessage());
                    }
                });
            }
        } catch (Exception e)
        {
            if(Customer.getCustomer() != null && Customer.getCustomer().customerId != null) {
                DatabaseReference errorRef = localRef.child("notification").child("error")
                        .child(Customer.getCustomer().customerId)
                        .child(String.valueOf(System.currentTimeMillis()));
                String currentStatus = e.getMessage();
                errorRef.setValue(currentStatus);
            }
        }

    }

    private void SetRoomKeywords() {
        try {
            if(keywordRoomList.size() == 0) {
                for (int i = 0; i < roomList.size(); i++) {
                    keywordRoomList.add((roomList.get(i).getRoomName()).toLowerCase());
                }
            }


        } catch (Exception e)
        {
            if(Customer.getCustomer() != null && Customer.getCustomer().customerId != null) {
                DatabaseReference errorRef = localRef.child("notification").child("error")
                        .child(Customer.getCustomer().customerId)
                        .child(String.valueOf(System.currentTimeMillis()));
                String currentStatus = e.getMessage();
                errorRef.setValue(currentStatus);
            }
        }
    }

    private void SetApplianceKeywords() {
        try {
            if(keywordApplianceList.size() == 0) {
                for (int i = 0; i < appliances.size(); i++) {
                    keywordApplianceList.add(appliances.get(i).getApplianceName());
                }
            }

        } catch (Exception e)
        {
            if(Customer.getCustomer() != null && Customer.getCustomer().customerId != null)  {
                DatabaseReference errorRef = localRef.child("notification").child("error")
                        .child(Customer.getCustomer().customerId)
                        .child(String.valueOf(System.currentTimeMillis()));
                String currentStatus = e.getMessage();
                errorRef.setValue(currentStatus);
            }
        }
    }

    private void SetStateKeywords() {
        try {
            keywordStateList.add("on");
            keywordStateList.add("off");
            keywordStateList.add("of");
            keywordStateList.add("open");
            keywordStateList.add("close");
            SetLevelKeywords();
        }
        catch (Exception e)
        {
            if(Customer.getCustomer() != null && Customer.getCustomer().customerId != null)  {
                DatabaseReference errorRef = localRef.child("notification").child("error")
                        .child(Customer.getCustomer().customerId)
                        .child(String.valueOf(System.currentTimeMillis()));
                String currentStatus = e.getMessage();
                errorRef.setValue(currentStatus);
            }
        }
    }

    private void SetLevelKeywords() {
        keywordLevelList.add("1");
        keywordLevelList.add("2");
        keywordLevelList.add("3");
        keywordLevelList.add("4");
        keywordLevelList.add("5");
    }

    private void GetCurtainList()
    {
        if(Customer.getCustomer() != null && Customer.getCustomer().customerId != null) {
            DatabaseReference curtainDetails = localRef.child("curtain")
                    .child(Customer.getCustomer().customerId).child("roomdetails");

            curtainDetails.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot detailsSnapshot : snapshot.getChildren()) {
                        for (DataSnapshot temp : detailsSnapshot.getChildren()) {

                            for (DataSnapshot type : temp.getChildren()) {

                                CurtainDetails curtain = type.getValue(CurtainDetails.class);
                                curtains.add(curtain);
                            }
                        }
                    }


                }

                @Override
                public void onCancelled(DatabaseError DatabaseError) {
                }
            });
        }

    }

    private void GetRoomList() {
        try {
            if (Customer.getCustomer() != null && Customer.getCustomer().customerId != null)
            {
                DatabaseReference roomDetails = localRef.child("rooms").child(Customer.getCustomer().customerId)
                        .child("roomdetails");

                roomDetails.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        roomList = new ArrayList<>();
                        for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
                            if (!roomSnapshot.getRef().toString().contains("globalOff")) {
                                if (Customer.getCustomer() != null && Customer.getCustomer().customerId != null)  {
                                    localRef.child("rooms").child(Customer.getCustomer().customerId).child("roomdetails");
                                    String roomType = roomSnapshot.child("roomType").getValue(String.class);
                                    String roomName = roomSnapshot.child("roomName").getValue(String.class);
                                    String roomId = roomSnapshot.child("roomId").getValue(String.class);
                                    RoomDetails room = new RoomDetails(roomName, roomType, roomId);
                                    roomList.add(room);
                                }
                            }

                        }
                        SetRoomKeywords();
                    }

                    @Override
                    public void onCancelled(DatabaseError DatabaseError) {
                        System.out.println("The read failed: " + DatabaseError.getMessage());
                    }
                });
            }
        } catch (Exception e)
        {
            if (Customer.getCustomer() != null && Customer.getCustomer().customerId != null) {
                DatabaseReference errorRef = localRef.child("notification").child("error")
                        .child(Customer.getCustomer().customerId)
                        .child(String.valueOf(System.currentTimeMillis()));
                String currentStatus = e.getMessage();
                errorRef.setValue(currentStatus);
            }
        }
    }

    private void GetMoodList() {
        if (Customer.getCustomer() != null && Customer.getCustomer().customerId != null) {
            DatabaseReference moodDetails = localRef.child("mode").child(Customer.getCustomer().customerId)
                    .child("modeDetails");
            moodDetails.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot detailsSnapshot : dataSnapshot.getChildren()) {

                        modeDetails mood = detailsSnapshot.getValue(modeDetails.class);
                        moods.add(mood);
                    }
                    SetMoodKeywords();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }


            });

        }
    }

    private void GetCustomerName()
    {
        if(Customer.getCustomer() != null &&  Customer.getCustomer().customerId != null) {
            DatabaseReference userDetails = GlobalApplication.firebaseRef.child("userdetials").child(Customer.getCustomer().customerId);
            userDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        if (userSnapshot.getRef().toString().contains("name")) {
                            customerName = userSnapshot.getValue(String.class);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError DatabaseError) {

                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startPhraseSpotting();
    }

    /**
     * Called to start the Phrase Spotter
     */
    private void startPhraseSpotting() {
        if ( phraseSpotterReader == null ) {
            phraseSpotterReader = new PhraseSpotterReader(new SimpleAudioByteStreamSource());
            phraseSpotterReader.setListener( phraseSpotterListener );
            phraseSpotterReader.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // if we don't, we must still be listening for "ok hound" so teardown the phrase spotter
        if ( phraseSpotterReader != null ) {
            stopPhraseSpotting();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // if we don't, we must still be listening for "ok hound" so teardown the phrase spotter
        if ( textToSpeechMgr != null ) {
            textToSpeechMgr.shutdown();
            textToSpeechMgr = null;
        }
    }

    /**
     * Called to stop the Phrase Spotter
     */
    private void stopPhraseSpotting() {
        if ( phraseSpotterReader != null ) {
            phraseSpotterReader.stop();
            phraseSpotterReader = null;
        }
    }

    /**
     * Implementation of the PhraseSpotterReader.Listener interface used to handle PhraseSpotter
     * call back.
     */
    private final PhraseSpotterReader.Listener phraseSpotterListener = new PhraseSpotterReader.Listener() {
        @Override
        public void onPhraseSpotted() {

            // It's important to note that when the phrase spotter detects "Ok Hound" it closes
            // the input stream it was provided.
            mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    stopPhraseSpotting();
                    // Now start the HoundifyVoiceSearchActivity to begin the search.
                    Houndify.get( HoundVoiceSearchExampleActivity.this ).voiceSearch( HoundVoiceSearchExampleActivity.this );
                }
            });
        }

        @Override
        public void onError(final Exception ex) {

            // for this sample we don't care about errors from the "Ok Hound" phrase spotter.

        }
    };

    /**
     * The HoundifyVoiceSearchActivity returns its result back to the calling Activity
     * using the Android's onActivityResult() mechanism.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Houndify.REQUEST_CODE) {
            final HoundSearchResult result = Houndify.get(this).fromActivityResult(resultCode, data);

            if (result.hasResult()) {
                onResponse( result.getResponse() );
            }
            else if (result.getErrorType() != null) {
                onError(result.getException(), result.getErrorType());
            }
            else {
                textView.setText("Aborted search");
            }
        }
    }

    /**
     * Called from onActivityResult() above
     *
     * @param response
     */
    private void onResponse(final HoundResponse response) {

        // Make sure the request succeeded with OK
        if ( response.getStatus().equals( HoundResponse.Status.OK ) ) {

            if (response.getResults().size() > 0) {
                // Required for conversational support
                StatefulRequestInfoFactory.get(this).setConversationState(response.getResults().get(0).getConversationState());
    
                textView.setText("Received response\n\n" + response.getResults().get(0).getWrittenResponse());
                textToSpeechMgr.speak(response.getResults().get(0).getSpokenResponse());

                text = response.getResults().get(0).getWrittenResponse();
                VoiceActions(text);
    
                /**
                 * "Client Match" demo code.
                 *
                 * Houndify client apps can specify their own custom phrases which they want matched using
                 * the "Client Match" feature. This section of code demonstrates how to handle
                 * a "Client Match phrase".  To enable this demo first open the
                 * StatefulRequestInfoFactory.java file in this project and and uncomment the
                 * "Client Match" demo code there.
                 *
                 * Example for parsing "Client Match"
                 */
                /*if (response.getResults().size() > 0) {
                    CommandResult commandResult = response.getResults().get(0);
                    if (commandResult.getCommandKind().equals("ClientMatchCommand")) {
                        JsonNode matchedItemNode = commandResult.getJsonNode().findValue("MatchedItem");
                        String intentValue = matchedItemNode.findValue("Intent").textValue();
    
                        if (intentValue.equals("TURN_LIGHT_ON")) {
                            textToSpeechMgr.speak("Client match TURN LIGHT ON successful");
                        } else if (intentValue.equals("TURN_LIGHT_OFF")) {
                            textToSpeechMgr.speak("Client match TURN LIGHT OFF successful");
                        }
                    }
                }*/
            }
            else {
                textView.setText("Received empty response!");
            }
        }
        else  {
            textView.setText( "Request failed with: " + response.getErrorMessage() );
        }

    }

    private void speakOut(String message)
    {
        if(message !=  null)
        {
            textToSpeechMgr.speak(message);
        }
    }

    private void UpdateFirebase(RoomDetails roomDetails, RoomAppliance roomAppliance, final Boolean stateBoolean) {
        try {
            if(Customer.getCustomer() != null &&  Customer.getCustomer().customerId != null) {
                DatabaseReference applianceRef = GlobalApplication.firebaseRef
                        .child("rooms").child(Customer.getCustomer().customerId).child("roomdetails").child(roomAppliance.getRoomId())
                        .child(roomAppliance.getSlaveId()).child("appliance").child(roomAppliance.getId()).child("toggle");


                applianceRef.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData currentData) {
                        currentData.setValue(stateBoolean ? 1 : 0);
                        return Transaction.success(currentData);
                        //we can also abort by calling Transaction.abort()
                    }

                    @Override
                    public void onComplete(DatabaseError DatabaseError, boolean committed, DataSnapshot currentData) {
                        Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_LONG).show();
                    }
                });
            }
        } catch (Exception e)
        {
            if(Customer.getCustomer() != null &&  Customer.getCustomer().customerId != null) {
                DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                        .child(String.valueOf(System.currentTimeMillis()));
                String currentStatus = e.getMessage();
                errorRef.setValue(currentStatus);
            }
        }
    }

    private void UpdateFirebaseDimmable(RoomDetails roomDetails, RoomAppliance roomAppliance, final String value) {
        if (roomAppliance.isState()) {

            final Integer integerValue = Integer.parseInt(value);
            if(Customer.getCustomer() != null &&  Customer.getCustomer().customerId != null) {

                DatabaseReference applianceRef = localRef
                        .child("rooms").child(Customer.getCustomer().customerId ).child("roomdetails").child(roomAppliance.getRoomId())
                        .child(roomAppliance.getSlaveId()).child("appliance").child(roomAppliance.getId()).child("dimableValue");

                applianceRef.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData currentData) {
                        if (currentData.getValue() == null) {
                            currentData.setValue(integerValue);
                        } else {
                            currentData.setValue(integerValue);
                        }
                        return Transaction.success(currentData); //we can also abort by calling Transaction.abort()
                    }

                    @Override
                    public void onComplete(DatabaseError DatabaseError, boolean committed, DataSnapshot currentData) {
                        //This method will be called once with the results of the transaction.
                    }
                });

                if(Customer.getCustomer() != null &&  Customer.getCustomer().customerId != null) {
                    DatabaseReference dimableRef = localRef
                            .child("rooms").child(Customer.getCustomer().customerId ).child("roomdetails").child(roomAppliance.getRoomId())
                            .child(roomAppliance.getSlaveId()).child("appliance").child(roomAppliance.getId()).child("dimableToggle");
                    dimableRef.setValue(1);

                }
            }
        }
    }

    private void VoiceActions(String match) {
        try {

            boolean matched = false;
            String error = "";
            try {


                    if (!match.equals("")) {
                        String[] words = match.split(" ");
                        List<String> wordList = new ArrayList<>(Arrays.asList(words));

                        for (int i = 0, l = wordList.size(); i < l; ++i) {
                            wordList.set(i, wordList.get(i).toLowerCase());
                        }
                        if (wordList.contains("increase") || wordList.contains("decrease") || wordList.contains("level")) {
                            if (!DisjointIgnoreCase.disjointIgnoreCase(wordList, keywordRoomList)
                                    && !DisjointIgnoreCase.disjointIgnoreCase(wordList, keywordApplianceList)) {
                                List<String> room = new ArrayList<>(wordList);
                                room.retainAll(DisjointIgnoreCase.lowercased(keywordRoomList));

                                List<String> level = new ArrayList<>(wordList);
                                level.retainAll(keywordLevelList);


                                List<String> appliance = new ArrayList<>(wordList);
                                appliance.retainAll(DisjointIgnoreCase.lowercased(keywordApplianceList));


                                RoomDetails roomDetails = new RoomDetails();
                                for (int i = 0; i < roomList.size(); i++) {
                                    if (roomList.get(i).getRoomName().toLowerCase().equals(room.get(0))) {
                                        roomDetails = roomList.get(i);
                                    }
                                }
                                RoomAppliance roomAppliance = new RoomAppliance();

                                for (int i = 0; i < appliances.size(); i++) {
                                    if (appliances.get(i).getApplianceName().toLowerCase().equals(appliance.get(0))
                                            && roomDetails.getRoomId().equals(appliances.get(i).getRoomId())) {
                                        roomAppliance = appliances.get(i);
                                    }
                                }

                                int currentLevel = 0;

                                if (roomAppliance.isDimmable()) {
                                    currentLevel = roomAppliance.getDimableValue();
                                }

                                if ((roomAppliance.isDimmable() && roomAppliance.getApplianceType().equals("Fan")) ||
                                        (roomAppliance.isDimmable() && roomAppliance.getApplianceType().equals("Light"))) {
                                    if (wordList.contains("level")) {
                                        if(roomAppliance.isState()) {
                                            if(level.size()> 0) {
                                                UpdateFirebaseDimmable(roomDetails, roomAppliance, level.get(0));
                                                matched = true;
                                                speakOut(customerName + " The" + roomAppliance.getApplianceName() + " has been turned to level " + level.get(0));
                                                return;
                                            }
                                            else
                                            {
                                                matched = true;
                                                speakOut(customerName + " you didn't specify the level correctly. Please try again.");
                                                return;
                                            }
                                        }
                                        else
                                        {
                                            matched = true;
                                            speakOut(customerName + " the" + roomAppliance.getApplianceName() + " is switched off. Please switch it on first.");
                                            return;
                                        }
                                    } else if (wordList.contains("increase")) {
                                        if(roomAppliance.isState()) {
                                            if (!(currentLevel == 5)) {
                                                int newLevel = 0;
                                                newLevel = currentLevel + 1;
                                                UpdateFirebaseDimmable(roomDetails, roomAppliance, String.valueOf(newLevel));

                                                matched = true;
                                                speakOut(customerName + " The" + roomAppliance.getApplianceName() + " has been turned to level " + String.valueOf(newLevel));
                                                return;
                                            } else {
                                                speakOut(customerName + "The" + roomAppliance.getApplianceName()
                                                        + " has been turned to the highest level. It can't be increased more.");
                                                matched = true;
                                            }
                                        }
                                        else
                                        {
                                            matched = true;
                                            speakOut(customerName + " the" + roomAppliance.getApplianceName() + " is switched off. Please switch it on first.");
                                            return;
                                        }
                                    } else if (wordList.contains("decrease") && !(currentLevel == 1)) {
                                        if(roomAppliance.isState()) {
                                            int newLevel = 0;
                                            newLevel = currentLevel - 1;
                                            UpdateFirebaseDimmable(roomDetails, roomAppliance, String.valueOf(newLevel));

                                            matched = true;
                                            speakOut(customerName + " The" + roomAppliance.getApplianceName() + " has been turned to level " + String.valueOf(newLevel));
                                            return;
                                        }else
                                        {
                                            matched = true;
                                            speakOut(customerName + " the" + roomAppliance.getApplianceName() + " is switched off. Please switch it on first.");
                                            return;
                                        }
                                    }
                                }
                            }

                        } else if (wordList.contains("energy")) {
                            List<String> room = new ArrayList<>(wordList);
                            room.retainAll(DisjointIgnoreCase.lowercased(keywordRoomList));


                            List<String> level = new ArrayList<>(wordList);
                            level.retainAll(keywordLevelList);


                            List<String> appliance = new ArrayList<>(wordList);
                            appliance.retainAll(DisjointIgnoreCase.lowercased(keywordApplianceList));

                            RoomDetails roomDetails = new RoomDetails();
                            for (int i = 0; i < roomList.size(); i++) {
                                if (roomList.get(i).getRoomName().toLowerCase().equals(room.get(0))) {
                                    roomDetails = roomList.get(i);
                                }
                            }
                            RoomAppliance roomAppliance = new RoomAppliance();

                            for (int i = 0; i < appliances.size(); i++) {
                                if (appliances.get(i).getApplianceName().toLowerCase().equals(appliance.get(0))
                                        && roomDetails.getRoomId().equals(appliances.get(i).getRoomId())) {
                                    roomAppliance = appliances.get(i);
                                }
                            }

                            if (roomAppliance.isState()) {

                                String energy = String.valueOf( ((Integer.parseInt(roomAppliance.getEnergy()))/5)*roomAppliance.getDimableValue());
                                speakOut(customerName + " the energy consumption of " + roomAppliance.getApplianceName()
                                        + " is" + energy + "Watts.");
                            } else {
                                speakOut("The appliance is switched off " + customerName);
                            }
                            matched = true;
                            return;

                        } else if (wordList.contains("mood"))
                        {
                            List<String> mood = new ArrayList<>(wordList);
                            mood.retainAll(DisjointIgnoreCase.lowercased(keywordMoodList));

                            List<String> state = new ArrayList<>(wordList);
                            state.retainAll(keywordStateList);

                            modeDetails theMood = new modeDetails();

                            for (int i = 0; i < moods.size(); i++) {
                                if (moods.get(i).getMoodName().toLowerCase().equals(mood.get(0))) {
                                    theMood = moods.get(i);
                                }
                            }
                            if(theMood != null) {

                                if (state.get(0).equals("on")) {

                                    if (!theMood.isState()) {
                                        UpdateFirebaseMood(theMood);
                                        matched = true;
                                        speakOut("here you go!!!");
                                        return;
                                    } else {
                                        matched = true;
                                        speakOut("The Mood is already ON");
                                        return;
                                    }
                                } else if (state.get(0).equals("off") || state.get(0).equals("of")) {
                                    if (!theMood.isState()) {
                                        UpdateFirebaseMood(theMood);
                                        matched = true;
                                        speakOut("here you go!!!");
                                        return;
                                    } else {
                                        matched = true;
                                        speakOut("The Mood is already OFF");
                                        return;
                                    }
                                }
                            }
                            else
                            {
                                matched = true;
                                speakOut("I did't understand the mood name. Can you please say it again?");
                                return;
                            }
                        }
                        else if(wordList.contains("curtain"))
                        {
                            List<String> room = new ArrayList<>(wordList);
                            room.retainAll(DisjointIgnoreCase.lowercased(keywordRoomList));


                            List<String> level = new ArrayList<>(wordList);
                            level.retainAll(keywordLevelList);

                            List<String> state = new ArrayList<>(wordList);
                            state.retainAll(keywordStateList);

                            CurtainDetails theCurtain = new CurtainDetails();
                            RoomDetails roomDetails = new RoomDetails();
                            for (int i = 0; i < roomList.size(); i++) {
                                if (roomList.get(i).getRoomName().toLowerCase().equals(room.get(0))) {
                                    roomDetails = roomList.get(i);
                                }
                            }

                            for (int i = 0; i < curtains.size(); i++) {
                                if (curtains.get(i).getCurtainName().toLowerCase().equals("curtain")
                                        && roomDetails.getRoomId().equals(curtains.get(i).getRoomId())) {
                                    theCurtain = curtains.get(i);
                                }
                            }

                            if(wordList.contains("close") && wordList.contains("full"))
                            {
                                UpdateCurtainFirebase(theCurtain,5);
                                matched = true;
                                return;
                            }else if((wordList.contains("open") || wordList.contains("close")) && wordList.contains("half"))
                            {
                                UpdateCurtainFirebase(theCurtain,2);
                                matched = true;
                                return;
                            }
                            else if(wordList.contains("open") && wordList.contains("full"))
                            {
                                UpdateCurtainFirebase(theCurtain,0);
                                matched = true;
                                return;
                            }
                        }
                        else {
                            if (!DisjointIgnoreCase.disjointIgnoreCase(wordList, keywordRoomList)
                                    && !DisjointIgnoreCase.disjointIgnoreCase(wordList, keywordStateList)
                                    && !DisjointIgnoreCase.disjointIgnoreCase(wordList, keywordApplianceList)) {
                                List<String> room = new ArrayList<>(wordList);
                                room.retainAll(DisjointIgnoreCase.lowercased(keywordRoomList));

                                List<String> state = new ArrayList<>(wordList);
                                state.retainAll(keywordStateList);

                                List<String> appliance = new ArrayList<>(wordList);
                                appliance.retainAll(DisjointIgnoreCase.lowercased(keywordApplianceList));


                                RoomDetails roomDetails = new RoomDetails();
                                for (int i = 0; i < roomList.size(); i++) {
                                    if (roomList.get(i).getRoomName().toLowerCase().equals(room.get(0))) {
                                        roomDetails = roomList.get(i);
                                    }
                                }
                                RoomAppliance roomAppliance = new RoomAppliance();

                                for (int i = 0; i < appliances.size(); i++) {
                                    if (appliances.get(i).getApplianceName().toLowerCase().equals(appliance.get(0))
                                            && roomDetails.getRoomId().equals(appliances.get(i).getRoomId())) {
                                        roomAppliance = appliances.get(i);
                                    }
                                }

                                if (state.get(0).equals("on") && !roomAppliance.isState()) {
                                    UpdateFirebase(roomDetails, roomAppliance, true);
                                    matched = true;
                                    return;
                                } else if ((state.get(0).equals("off") && roomAppliance.isState())
                                        || (state.get(0).equals("of") && roomAppliance.isState())) {
                                    UpdateFirebase(roomDetails, roomAppliance, true);
                                    matched = true;
                                    speakOut(customerName + " The " + roomAppliance.getApplianceName() + " has been turned " + state.get(0));
                                    return;
                                } else
                                    matched = true;
                                speakOut(customerName + " The " + roomAppliance.getApplianceName() + " is already " + state.get(0));
                                return;
                            }
                        }
                    }
                if (!matched) {
                    speakOut("So Sorry"+customerName+ "please tell me again correctly");
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Voice Activity Exception", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Voice Activity Exception", Toast.LENGTH_LONG).show();
        }
    }


    private void UpdateFirebaseMood(modeDetails theMood)
    {
        String[] modeAppliances = theMood.getApplianceId().split(",");
        String offAppliances = "";

        for (int i = 0; i < appliances.size(); i++) {
            if (appliances.get(i).getRoomId().equals(theMood.getRoomId()) &&
                    appliances.get(i).isState() && !(Arrays.asList(modeAppliances).contains(appliances.get(i).getId()))) {
                offAppliances = offAppliances + appliances.get(i).getId() + ",";
            }
        }
        if (!offAppliances.isEmpty()) {

            offAppliances = offAppliances.substring(0, offAppliances.length() - 1);
        }

        final String switchOffAppliances = offAppliances;

        if(Customer.getCustomer() != null &&  Customer.getCustomer().customerId != null) {


            DatabaseReference offAppliancesRef = localRef
                    .child("mode").child(Customer.getCustomer().customerId).child("modeDetails")
                    .child(String.valueOf(theMood.getModeId())).child("offAppliances");

            offAppliancesRef.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData currentData) {
                    currentData.setValue(switchOffAppliances);
                    return Transaction.success(currentData);
                    //we can also abort by calling Transaction.abort()
                }

                @Override
                public void onComplete(DatabaseError DatabaseError, boolean committed, DataSnapshot currentData) {
                    //This method will be called once with the results of the transaction.
                }
            });

            DatabaseReference applianceRef = localRef
                    .child("mode").child(Customer.getCustomer().customerId).child("modeDetails").child(String.valueOf(theMood.getModeId())).child("toggle");

            applianceRef.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData currentData) {
                    currentData.setValue(1);
                    return Transaction.success(currentData);
                    //we can also abort by calling Transaction.abort()
                }

                @Override
                public void onComplete(DatabaseError DatabaseError, boolean committed, DataSnapshot currentData) {
                    //This method will be called once with the results of the transaction.
                }
            });
        }
    }

    private void UpdateCurtainFirebase(CurtainDetails curtainDetails, final int level)
    {
        CurtainDetails curtainDetails1 = curtains.get(position);
        if(Customer.getCustomer() != null &&  Customer.getCustomer().customerId != null) {

            DatabaseReference curtainRef = GlobalApplication.firebaseRef
                    .child("curtain").child(Customer.getCustomer().customerId).child("roomdetails").child(curtainDetails1.getRoomId()).child("curtainDetails")
                    .child(curtainDetails.getCurtainId()).child("curtainLevel");

            curtainRef.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    if (mutableData.getValue() == null) {
                        mutableData.setValue(level);
                    } else {
                        mutableData.setValue(level);
                    }
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError DatabaseError, boolean b, DataSnapshot dataSnapshot) {

                }
            });

            if(Customer.getCustomer() != null &&  Customer.getCustomer().customerId != null) {

                DatabaseReference dimableRef = GlobalApplication.firebaseRef
                        .child("curtain").child(Customer.getCustomer().customerId).child("roomdetails").child(curtainDetails1.getRoomId()).child("curtainDetails")
                        .child(curtainDetails.getCurtainId()).child("toggle");
                dimableRef.setValue(1);

            }
        }
    }


    /**
     * Called from onActivityResult() above
     *
     * @param ex
     * @param errorType
     */
    private void onError(final Exception ex, final VoiceSearchInfo.ErrorType errorType) {
        textView.setText(errorType.name() + "\n\n" + exceptionToString(ex));
    }

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


    /**
     * Helper class used for managing the TextToSpeech engine
     */
    class TextToSpeechMgr implements TextToSpeech.OnInitListener {
        private TextToSpeech textToSpeech;

        public TextToSpeechMgr( Activity activity ) {
            textToSpeech = new TextToSpeech( activity, this );
        }

        @Override
        public void onInit( int status ) {
            // Set language to use for playing text
            if ( status == TextToSpeech.SUCCESS ) {
                int result = textToSpeech.setLanguage(Locale.ENGLISH);
            }
        }

        public void shutdown() {
            textToSpeech.shutdown();
        }
        /**
         * Play the text to the device speaker
         *
         * @param textToSpeak
         */
        public void speak( String textToSpeak ) {
            textToSpeech.speak(textToSpeak, TextToSpeech.QUEUE_ADD, null);
        }
    }
}
