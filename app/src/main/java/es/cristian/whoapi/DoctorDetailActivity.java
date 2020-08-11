package es.cristian.whoapi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

public class DoctorDetailActivity extends AppCompatActivity {

    public static final String DOCTOR = "doctor";

    public static Intent newIntent(Context context,Doctor doctor){
        Intent intent = new Intent(context,DoctorDetailActivity.class);
        intent.putExtra(DOCTOR, doctor);
        return intent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        Doctor doctor = getIntent().getParcelableExtra(DOCTOR);
        Toast.makeText(this,"doctor"+doctor, Toast.LENGTH_LONG).show();
    }
}
