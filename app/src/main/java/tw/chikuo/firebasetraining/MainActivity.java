package tw.chikuo.firebasetraining;

import android.content.ComponentCallbacks;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Query;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Firebase myFirebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup Firebase library
        Firebase.setAndroidContext(this);

        // Read & Write to your Firebase Database
        myFirebaseRef = new Firebase("https://fir-training-85477.firebaseio.com/");

        /**
         *  Saving & Retrieving data
         */

//        writeData();
//        writeDataWithListener();

//        updateData();
//        updateDataWithListener();

//        writeDataWithUniqueID();

//        updateDataCount();

//        valueEventListener();

//        childEventListener();

//        readingDataOnce();

//        detachingCallbacks();


        /**
         *  Querying Data
         *
         *  Ordering functions:
         *  orderByChild(), orderByKey(), orderByValue(), or orderByPriority().
         *  You can then combine these with five other methods :
         *  limitToFirst(), limitToLast(), startAt(), endAt(), and equalTo().
         */


        myFirebaseRef = new Firebase("https://fir-training-85477.firebaseio.com/users");

        // Ordering by a specified child key
        Query queryRef = myFirebaseRef.orderByChild("age");

        // Can use the full path to the object rather than a single key
        // Query queryRef = myFirebaseRef.orderByChild("department/sales");

        // Limit Queries : limitToFirst() , limitToLast()
        // Query queryRef = myFirebaseRef.orderByChild("age").limitToFirst(2);

        // Range Queries : startAt() , endAt() , equalTo()
        // Query queryRef = myFirebaseRef.orderByChild("age").startAt(20).endAt(25);
        // Query queryRef = myFirebaseRef.orderByKey().startAt("1").endAt("3");

        // Equal Queries
        // Query queryRef = myFirebaseRef.orderByChild("age").equalTo(24);

        // Queries can only order by one key at a time
        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                User user = snapshot.getValue(User.class);
                Log.d("FireBaseTraining", "name = " + user.getName() + " , Age = " + user.getAge());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


    }

    private void detachingCallbacks() {
//        myFirebaseRef.removeEventListener(originalListener);
    }

    private void readingDataOnce() {
        // In some cases it may be useful for a callback to be called once and then immediately removed.
        // We can use addListenerForSingleValueEvent() to make this easy.
        // It is triggered one time and then will not be triggered again..
        myFirebaseRef = new Firebase("https://fir-training-85477.firebaseio.com/users");
        myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // do some stuff once
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    Log.d("FireBaseTraining", "name = " + user.getName() + " , Age = " + user.getAge());
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    private void childEventListener() {

        myFirebaseRef = new Firebase("https://fir-training-85477.firebaseio.com/users");
        myFirebaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                User user = dataSnapshot.getValue(User.class);
                Log.d("FireBaseTraining", "Added : name = " + user.getName() + " , Age = " + user.getAge());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                User user = dataSnapshot.getValue(User.class);
                Log.d("FireBaseTraining", "Changed : name = " + user.getName() + " , Age = " + user.getAge());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Log.d("FireBaseTraining", "Removed : name = " + user.getName() + " , Age = " + user.getAge());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
                User user = dataSnapshot.getValue(User.class);
                Log.d("FireBaseTraining", "Moved : name = " + user.getName() + " , Age = " + user.getAge());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void valueEventListener() {
        // This method will be called anytime new data is added to our Firebase reference
        myFirebaseRef = new Firebase("https://fir-training-85477.firebaseio.com/users");
        myFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("FireBaseTraining", " snapshot.getValue() = " + snapshot.getValue());

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    Log.d("FireBaseTraining", "name = " + user.getName() + " , Age = " + user.getAge());
                }

            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d("FireBaseTraining", "The read failed: " + firebaseError.getMessage());
            }
        });
    }

    private void updateDataCount() {
        // When working with complex data that could be corrupted by concurrent modifications,
        // such as incremental counters, we provide a transaction operation.

        Firebase countRef = myFirebaseRef.child("userCount");
        countRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData currentData) {
                if(currentData.getValue() == null) {
                    currentData.setValue(1);
                } else {
                    currentData.setValue((Long) currentData.getValue() + 1);
                }

                return Transaction.success(currentData); //we can also abort by calling Transaction.abort()
            }

            @Override
            public void onComplete(FirebaseError firebaseError, boolean committed, DataSnapshot currentData) {
                //This method will be called once with the results of the transaction.
            }
        });

    }

    private void writeDataWithUniqueID() {
        // Getting the Unique ID Generated by push()

        Firebase userRef = myFirebaseRef.child("users");
        User user = new User("Addy", 20);
        userRef.push().setValue(user);

    }

    private void updateDataWithListener() {
        // If you want to write to specific children of a node at the same time
        // without overwriting other child nodes, you can use the updateChildren() method .

        Firebase userRef = myFirebaseRef.child("users").child("1");
        Map<String, Object> nameMap = new HashMap<String, Object>();
        nameMap.put("name", "Chi Kuo");
        userRef.updateChildren(nameMap, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Log.d("FireBaseTraining", "Data could not be saved. " + firebaseError.getMessage());
                } else {
                    Log.d("FireBaseTraining", "Data saved successfully.");
                }
            }
        });
    }

    private void updateData() {
        // If you want to write to specific children of a node at the same time
        // without overwriting other child nodes, you can use the updateChildren() method .

        Firebase userRef = myFirebaseRef.child("users").child("1");
        Map<String, Object> nameMap = new HashMap<String, Object>();
        nameMap.put("name", "Chi Kuo");
        userRef.updateChildren(nameMap);
    }

    private void writeDataWithListener() {
        // Using setValue() will overwrite the data at the specified location, including any child nodes.

        Firebase userRef = myFirebaseRef.child("users").child("1");
        User user = new User("Amy", 24);
        userRef.setValue(user, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Log.d("FireBaseTraining", "Data could not be saved. " + firebaseError.getMessage());
                } else {
                    Log.d("FireBaseTraining", "Data saved successfully.");
                }
            }
        });
    }

    private void writeData() {
        // Using setValue() will overwrite the data at the specified location, including any child nodes.

        Firebase userRef = myFirebaseRef.child("users").child("1");
        User user = new User("Amy", 24);
        userRef.setValue(user);
    }
}
