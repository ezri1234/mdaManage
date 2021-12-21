package com.ezrimo.mdamanage;

import static java.lang.Long.parseLong;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class LoginTabFragment extends Fragment {
    Button loginButton;
    EditText email, password;
    boolean valid;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.login_tab_fragment, container, false);
        loginButton = root.findViewById(R.id.loginB);
        email = root.findViewById(R.id.email);
        password = root.findViewById(R.id.password);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkField(email);
                checkField(password);

                if (valid){
                    fAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(getContext(), "logged in successfully", Toast.LENGTH_SHORT).show();
                            isAdmin(authResult.getUser().getUid());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
        return root;
    }
    /*
    checks if fields are acceptable
    @par EditText
    @returns boolean
     */
    public boolean checkField(EditText textField){
        if(textField.getText().toString().isEmpty()){
            textField.setError("Error");
            valid = false;
        } else {
            valid = true;
        }
        return valid;
    }
    public void isAdmin (String uid) {
        DocumentReference dr = fStore.collection("Users").document(uid);
        dr.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("TAG", "on success" + documentSnapshot.getData());
                HashMap<String, Object> usersMap = (HashMap<String, Object>) documentSnapshot.getData().get("Users");
                Log.d("TAG", usersMap.get("fullName").toString());
                //documentSnapshot.get("fullName");
                if(parseLong(usersMap.get("isAdmin").toString())==1){
                    //user is an admin
                    startActivity(new Intent(getContext(), adminActivity.class));
                    getActivity().finish();
                }
                if (parseLong(usersMap.get("isAdmin").toString())==0){
                    //user isnt an admin
                    startActivity(new Intent (getActivity(), RegularUserActivity.class));
                    getActivity().finish();
                }
            }
        });

    }

}
