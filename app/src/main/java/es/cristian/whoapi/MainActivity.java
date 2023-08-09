package es.cristian.whoapi;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.security.ProviderInstaller;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private DoctorService doctorService;
    private DoctorsAdapter doctorsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ProviderInstaller.installIfNeededAsync(this, new ProviderInstaller.ProviderInstallListener() {
            @Override
            public void onProviderInstalled() {}

            @Override
            public void onProviderInstallFailed(int errorCode, Intent recoveryIntent) {}
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.catalogopolis.xyz/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        doctorService = retrofit.create(DoctorService.class);

        final RecyclerView recyclerView = findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));
        doctorsAdapter=new DoctorsAdapter(new ArrayList<>());
        recyclerView.setAdapter(doctorsAdapter);

        doctorService.all()
                .enqueue(new Callback<List<Doctor>>(){
                   @Override
                    public void onResponse(@NonNull Call<List<Doctor>> call, @NonNull Response<List<Doctor>> response){
                       Log.d(MainActivity.class.getSimpleName(), "Respuesta:"+response.body());
                       assert response.body() != null;
                       doctorsAdapter.swapData(response.body());
                   }

                   @Override
                    public void onFailure(@NonNull Call<List<Doctor>> call, @NonNull Throwable t){
                   }
                });
    }

    private void actorForDoctor(final Doctor doctor) {
        doctorService.doctorActor(doctor.id)
                .enqueue(new Callback<List<Actor>>(){
                    @Override
                    public void onResponse(@NonNull Call<List<Actor>> actorCall, @NonNull Response<List<Actor>> actorResponse){
                        List<Actor> actors=actorResponse.body();
                        doctor.setActors(actors);
                        doctorsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Actor>> call, @NonNull Throwable t){
                    }
                });
    }

    public class DoctorsAdapter extends RecyclerView.Adapter<DoctorsAdapter.DoctorViewHolder>{
        private List<Doctor>doctors;

        void swapData(List<Doctor> doctors) {
            this.doctors=doctors;
            for (final Doctor doctor: doctors){
                actorForDoctor(doctor);
            }
            notifyDataSetChanged();
        }

        class DoctorViewHolder extends RecyclerView.ViewHolder{
            TextView nameText;
            TextView aliasText;
            LinearLayout doctorLinearLayout;

            DoctorViewHolder(View itemView){
                super(itemView);
                nameText=itemView.findViewById(R.id.name);
                aliasText=itemView.findViewById(R.id.alias);
                doctorLinearLayout=itemView.findViewById(R.id.Actores);
            }
        }

        DoctorsAdapter(List<Doctor> doctors){
            this.doctors=doctors;
        }

        @Override
        @NonNull
        public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view=LayoutInflater.from(MainActivity.this).inflate(R.layout.list_doctors_item,parent,false);
            return new DoctorViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
            final Doctor doctor=doctors.get(position);
            holder.nameText.setText(doctor.alias);
            holder.aliasText.setText(String.valueOf(position+1));

            holder.doctorLinearLayout.removeAllViews();
            if (null!=doctor.actors){
                for (Actor actor:doctor.actors){
                    View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.list_actors_items, holder.doctorLinearLayout, false);
                    TextView nameTextView= view.findViewById(R.id.ActorName);
                    nameTextView.setText(actor.name);

                    holder.doctorLinearLayout.addView(view);
                }
            }

            holder.itemView.setOnClickListener(v -> startActivity(DoctorDetailActivity.newIntent(MainActivity.this, doctor)));
        }

        @Override
        public int getItemCount() {
            return doctors.size();
        }
    }
}
