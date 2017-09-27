package com.rollingdice.deft.android.tab;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;
import com.rollingdice.deft.android.tab.model.Customer;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.firebase.auth.FirebaseAuth.*;
import static java.security.MessageDigest.getInstance;


@SuppressWarnings("ConstantConditions")
public class AddUserDetails extends Activity {

    private MaterialEditText etUserName;
    private MaterialEditText etPhoneNumber;
    private MaterialEditText etEmail;
    private MaterialEditText etLockPassword;
    private MaterialEditText etUserPassword;
    private MaterialEditText etConfirmedPassword;
    private MaterialEditText etAdminUserId;
    private MaterialEditText etAdminUserPassword;
    private MaterialEditText etIPAddress;
    private MaterialEditText etPort;
    private DatabaseReference localRef;
    private FirebaseAuth mAuth;
    public static String ipAddress;
    private SimpleArcDialog dialog;
    public static int port;
    private String lockPassword;
    private Snackbar bar;
    private Context context;
    private Dialog authDialog;
    private Customer customer;
    private boolean phoneValidFlag = true;


    private LinearLayout linearLayout;
    private StringBuffer dialogMsg = new StringBuffer();

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.add_user_details);
            context = this;


            linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

            bar = Snackbar.make(linearLayout, "Authentication Error", Snackbar.LENGTH_LONG)
                    .setAction("Dismiss", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bar.dismiss();
                        }
                    });


            View snackBarView = bar.getView();
            snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.red_1));


            dialog = new SimpleArcDialog(this);
            ArcConfiguration configuration = new ArcConfiguration(this);
            configuration.setLoaderStyle(SimpleArcLoader.STYLE.SIMPLE_ARC);
            configuration.setText("Login Process...");
            configuration.setAnimationSpeedWithIndex(SimpleArcLoader.SPEED_SLOW);
            dialog.setConfiguration(configuration);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);

            localRef = FirebaseDatabase.getInstance().getReference().getRoot();
            mAuth = FirebaseAuth.getInstance();


            etUserName = (MaterialEditText) findViewById(R.id.et_user_name);
            etPhoneNumber = (MaterialEditText) findViewById(R.id.et_phone_number);
            etEmail = (MaterialEditText) findViewById(R.id.et_email);
            etLockPassword = (MaterialEditText) findViewById(R.id.et_user_id);
            etUserPassword = (MaterialEditText) findViewById(R.id.et_password);
            etConfirmedPassword = (MaterialEditText) findViewById(R.id.et_confirm_password);
            etAdminUserId = (MaterialEditText) findViewById(R.id.et_admin_user_id);
            etAdminUserPassword = (MaterialEditText) findViewById(R.id.et_admin_user_password);
            etIPAddress = (MaterialEditText) findViewById(R.id.et_user_ipAddress);
            etPort = (MaterialEditText) findViewById(R.id.et_user_port);


            ///////////////////////////////////////////////////////////////////// Add User Details to form


            customer = new Select().from(Customer.class).executeSingle();

            if (customer != null) {

                etEmail.setText(customer.customerEmail);
                etEmail.setEnabled(false);

                etUserPassword.setText(customer.customerPassword);
                etUserPassword.setEnabled(false);

                etConfirmedPassword.setText(customer.customerPassword);
                etConfirmedPassword.setEnabled(false);

                etUserName.setText(customer.name);
                etPhoneNumber.setText(customer.customerPhone);
                etIPAddress.setText(customer.ipAddress);
                etPort.setText("" + customer.port);


                DatabaseReference lockRef = localRef.child("userdetials").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("lockPassword");
                lockRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            lockPassword = dataSnapshot.getValue(String.class);
                            etLockPassword.setText(dataSnapshot.getValue(String.class));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }


///////////////////////////////////////////////////////////////////////////////////////////////////////////


            findViewById(R.id.btnNext).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
/////////////// Create new  User

                    if (customer == null) {

                        validateORUpdateUserData();

                        if (dialogMsg.length() > 1) {
                            dialogMsg.deleteCharAt(dialogMsg.length() - 1);
                            getDialogWithoutTitle("Please Enter " + dialogMsg.toString()).show();
                            dialogMsg = new StringBuffer();

                        } else {
                            if(isValidMail(etEmail.getText().toString()) && isValidMobile(etPhoneNumber.getText().toString())){
                                if (validatePassword()) {

                                    dialog.show();
                                    createUserInFireBase();

                                } else {
                                    getDialogWithoutTitle("Confirm password And password does not match please check ").show();
                                }
                            }else {
                                if(!isValidMail(etEmail.getText().toString())){
                                    getDialogWithoutTitle("Wrong Email Please Check");
                                }else if(isValidMobile(etPhoneNumber.getText().toString())){
                                    getDialogWithoutTitle("Wrong Mobile Number Please Check");
                                }
                            }



                        }
//////////////////////////////////////
                    } else {

//////////////// Update User Data
                        validateORUpdateUserData();
                        if (dialogMsg.length() > 1) {
                            dialogMsg.deleteCharAt(dialogMsg.length() - 1);
                            getDialogWithoutTitle("Please Enter " + dialogMsg.toString()).show();
                            dialogMsg = new StringBuffer();

                        } else {

                            if (!lockPassword.equals(etLockPassword.getText().toString())) {

                                DatabaseReference lockRef = localRef.child("userdetials").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                Map<String, Object> details = new HashMap<>();
                                details.put("lockPassword", lockPassword);
                                lockRef.updateChildren(details);

                            } else if (!etPort.getText().toString().equals("" + customer.port)) {

                                localRef.child("IPAddress").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("port").
                                        setValue(Integer.parseInt(etPort.getText().toString()));
                                localRef.child("userdetials").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                                        child("port").setValue(Integer.parseInt(etPort.getText().toString()));

                                /// change from db
                                Customer model = selectField("port", "" + customer.port);
                                model.port = Integer.parseInt(etPort.getText().toString());
                                model.save();


                            } else if (!etIPAddress.getText().toString().equals(customer.ipAddress)) {

                                localRef.child("IPAddress").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("ipAddres").setValue(etIPAddress.getText().toString());
                                localRef.child("userdetials").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                                        child("ipAddress").setValue(etIPAddress.getText().toString());


                                /// change from db
                                Customer model = selectField("ipAddress", customer.ipAddress);
                                model.ipAddress = etIPAddress.getText().toString();
                                model.save();

                            } else if (!etUserName.getText().toString().equals(customer.name)) {

                                localRef.child("userdetials").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                                        child("name").setValue(etUserName.getText().toString());


                                /// change from db
                                Customer model = selectField("customerName", customer.name);
                                model.name = etUserName.getText().toString();
                                model.save();


                            } else if (!etPhoneNumber.getText().toString().equals(customer.customerPhone)) {

                                if(isValidMobile(etPhoneNumber.getText().toString())){
                                    localRef.child("userdetials").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                                            child("customerPhone").setValue(etPhoneNumber.getText().toString());

                                    Customer model = selectField("customerPhone", customer.customerPhone);
                                    model.customerPhone = etPhoneNumber.getText().toString();
                                    model.save();
                                }else {
                                    getDialogWithoutTitle("Please Check your Mobile Number");
                                    phoneValidFlag  = false;

                                }


                            }

                            if(phoneValidFlag){
                                updateUserData();
                            }


                        }

                        // updateUserData();
//////////////////////////////
                    }


                }
            });


        } catch (Exception e) {
            //Log.e("AddUserDetails",e.getMessage());

            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public Customer selectField(String fieldName, String fieldValue) {

        if (fieldName.equals("port")) {
            return new Select().from(Customer.class)
                    .where(fieldName + " = ?", Integer.parseInt(fieldValue)).executeSingle();
        } else {
            return new Select().from(Customer.class)
                    .where(fieldName + " = ?", fieldValue).executeSingle();
        }
    }


    private boolean isValidMail(String email) {
        boolean check;
        Pattern p;
        Matcher m;

        String EMAIL_STRING = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        p = Pattern.compile(EMAIL_STRING);

        m = p.matcher(email);
        check = m.matches();

        if(!check) {
            etEmail.setError("Not Valid Email");
        }
        return check;
    }


    private boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }


    public void validateORUpdateUserData() {


        if (lockPassword == null) {

            lockPassword = "";
        } else {
            lockPassword = etLockPassword.getText().toString();
        }

        if (etUserName.getText().toString() == null || etUserName.getText().toString().equals("")) {
            dialogMsg.append("Username,");
        }

        if (etPhoneNumber.getText().toString() == null || etPhoneNumber.getText().toString().equals("")) {
            dialogMsg.append("Phone Number,");
        }

        if (etEmail.getText().toString() == null || etEmail.getText().toString().equals("") && customer == null) {
            dialogMsg.append("Email ID,");
        }

        if (etUserPassword.getText().toString() == null || etUserPassword.getText().toString().equals("") && customer == null) {
            dialogMsg.append("User Password,");
        }
        if (etConfirmedPassword.getText().toString() == null || etConfirmedPassword.getText().toString().equals("") && customer == null) {
            dialogMsg.append("Confirm Password,");
        }

        if (etIPAddress.getText().toString() == null || etIPAddress.getText().toString().equals("")) {
            dialogMsg.append("IP Address,");
        } else {
            ipAddress = etIPAddress.getText().toString();
        }


        if (etPort.getText().toString() == null || etPort.getText().toString().equals("") || etPort.getText().toString().equals(" ")) {
            dialogMsg.append("Port,");
        } else {

            String text = etPort.getText().toString();
            port = Integer.parseInt(text);
        }


    }


    public void updateUserData() {


        if (customer != null) {


////     update Data field from

            startActivity(new Intent(this, RoomDetailsActivity.class));
            finish();
        }

    }

    private void validateAdmin(String adminUserId, final String password) {
        String encodedEmail = adminUserId.replace(".", ",");

        DatabaseReference adminUid = localRef.child("ADMINS").child(encodedEmail);


        if (adminUid != null) {

            adminUid.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    if (snapshot.getValue() != null) {
                        String uid = (String) snapshot.getValue();

                        DatabaseReference passwordRef = localRef.child("userdetails").child(uid).child("customerPassword");

                        passwordRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                String validPass = (String) snapshot.getValue();

                                //if (validPass != null && validPass.equals(getMD5Hash(password))) {
                                if (validPass != null && validPass.equals(password)) {

                                    if (validatePassword()) {
                                        createUserInFireBase();
                                    } else {
                                        Toast.makeText(AddUserDetails.this, getString(R.string.error_passwords_mismatched), Toast.LENGTH_LONG).show();
                                    }

                                } else {

                                    Toast.makeText(AddUserDetails.this, "Admin User password missmatch", Toast.LENGTH_LONG).show();


                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError DatabaseError) {

                            }
                        });

                    } else {
                        Toast.makeText(AddUserDetails.this, "Admin User Name is incorrect", Toast.LENGTH_LONG).show();


                    }


                }

                @Override
                public void onCancelled(DatabaseError DatabaseError) {


                }
            });
        }


    }


    private String getDeviceId() {
        UUID deviceUuid = UUID.randomUUID();
        try {


            final TelephonyManager telephonyManager = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

            final String tmDevice, tmSerial, androidId;
            tmDevice = "" + telephonyManager.getDeviceId();
            tmSerial = "" + telephonyManager.getSimSerialNumber();
            androidId = "" + Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());

        } catch (Exception e) {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }
        return deviceUuid.toString();
    }

    private void GoToNextScreen() {
        try {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            if (user != null && id != null) {
                // User is signed in

                DatabaseReference ipRef = localRef.child("IPAddress").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                Map<String, Object> IPdetails = new HashMap<>();
                IPdetails.put("ipAddres", ipAddress);
                IPdetails.put("port", port);
                ipRef.setValue(IPdetails);

                Toast.makeText(AddUserDetails.this, "User ID: " + user.getUid(), Toast.LENGTH_LONG).show();

                DatabaseReference service = localRef.child("ServiceStart").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                Map<String, Object> serviceReset = new HashMap<>();
                serviceReset.put("service", 0);
                service.setValue(serviceReset);


                saveAllInputs(user.getUid());
                startActivity(new Intent(AddUserDetails.this, RoomDetailsActivity.class).putExtra("CUSTOMER_ID", user.getUid()));
            } else {
                // User is signed out

                Toast.makeText(AddUserDetails.this, "Error", Toast.LENGTH_SHORT).show();


            }
        } catch (Exception e) {
            Toast.makeText(AddUserDetails.this, "Error" + e.getStackTrace(), Toast.LENGTH_SHORT).show();
        }
    }


    private String getMD5Hash(String password) {

        MessageDigest md;
        md = null;
        StringBuilder sb = new StringBuilder();
        try {
            md = getInstance("MD5");
            md.update(password.getBytes());

            byte byteData[] = md.digest();


            for (byte aByteData : byteData) {
                sb.append(Integer.toString((aByteData & 0xff) + 0x100, 16).substring(1));
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return sb.toString();

    }

    private void saveAllInputs(final String uid) {
        if (validatePassword()) {
            try {
                String inputPassword = etConfirmedPassword.getText().toString();

                Customer customer = new Customer();
                customer.name = etUserName.getText().toString();
                customer.customerPhone = etPhoneNumber.getText().toString();
                customer.customerEmail = etEmail.getText().toString();
                customer.customerId = uid;

                //customer.customerPassword = getMD5Hash(etConfirmedPassword.getText().toString());
                customer.customerPassword = etConfirmedPassword.getText().toString();
                customer.customerMasterDeviceId = "012";
                customer.userType = "USER";
                customer.ipAddress = ipAddress;
                customer.port = port;

                //Save in Fire Base.
                DatabaseReference userDetails = localRef.child("userdetials").child(uid);
                userDetails.setValue(customer);

                //Save lock password
                DatabaseReference lockRef = localRef.child("userdetials").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                Map<String, Object> details = new HashMap<>();
                details.put("lockPassword", lockPassword);
                lockRef.updateChildren(details);


                DatabaseReference masterController = localRef.child("masterControllerCnt");
                masterController.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        String value;

                        if (snapshot.getValue() == null) {
                            value = "0";

                        } else {
                            value = snapshot.getValue().toString();
                        }

                        int num = Integer.parseInt(value);
                        num++;

                        String masterController = String.format("%03X", num & 0xFFF);
                        DatabaseReference masterControllerRef = localRef.child("masterController").child(uid);
                        masterControllerRef.setValue("012");

                        DatabaseReference masterControllerCnt = localRef.child("masterControllerCnt");

                        masterControllerCnt.runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData currentData) {
                                if (currentData.getValue() == null) {
                                    currentData.setValue(1);
                                } else {
                                    currentData.setValue((Long) currentData.getValue() + 1);
                                }
                                return Transaction.success(currentData); //we can also abort by calling Transaction.abort()
                            }

                            @Override
                            public void onComplete(DatabaseError DatabaseError, boolean committed, DataSnapshot currentData) {
                                //This method will be called once with the results of the transaction.
                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError DatabaseError) {
                    }
                });
                customer.save();

                loginTo(customer.customerEmail, inputPassword);


            } catch (Exception e) {
                DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                        .child(String.valueOf(System.currentTimeMillis()));
                String currentStatus = e.getMessage();
                errorRef.setValue(currentStatus);
            }

        } else {
            Toast.makeText(this, getString(R.string.error_passwords_mismatched), Toast.LENGTH_LONG).show();
        }
    }

    private void loginTo(String userEmail, String password) {
        try {

            mAuth.signInWithEmailAndPassword(userEmail, password);
            AuthStateListener mAuthListener = new AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        // User is signed in
                        Toast.makeText(AddUserDetails.this, "User ID: " + user.getUid(), Toast.LENGTH_LONG).show();
                        startActivity(new Intent(AddUserDetails.this, RoomDetailsActivity.class).putExtra("CUSTOMER_ID", user.getUid()));
                        finish();

                    } else {
                        if (user.getUid() != null) {
                            // User is signed out
                            Toast.makeText(AddUserDetails.this, new StringBuilder().append("Signed Out ").append(user.getUid()).toString(), Toast.LENGTH_LONG).show();
                        }

                    }


                }
            };

        } catch (Exception e) {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }
    }

    private boolean validatePassword() {

        return (etConfirmedPassword.getText().toString().equals(etUserPassword.getText().toString()));
    }

    private void createUserInFireBase() {
        try {

            mAuth.createUserWithEmailAndPassword(etEmail.getText().toString(), etConfirmedPassword.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                dialog.dismiss();
                                Toast.makeText(AddUserDetails.this, "Authentication Successful", Toast.LENGTH_SHORT).show();


                                ///  Add Nodes into firebase
                                GoToNextScreen();

                            } else {

                                dialog.dismiss();

                                getDialogWithoutTitle("Authentication Error try again").show();

                               /* Toast.makeText(AddUserDetails.this, "AuthenticationError", Toast.LENGTH_SHORT).show();*/
                            }


                        }
                    });

        } catch (Exception e) {
            DatabaseReference errorRef = localRef.child("notification").child("error").child(Customer.getCustomer().customerId)
                    .child(String.valueOf(System.currentTimeMillis()));
            String currentStatus = e.getMessage();
            errorRef.setValue(currentStatus);
        }
    }


    private Dialog getDialogWithoutTitle(String msg) {
        return new AlertDialog.Builder(this)
//Please Check your password and try again
                .setMessage(msg)
                .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();

    }


}