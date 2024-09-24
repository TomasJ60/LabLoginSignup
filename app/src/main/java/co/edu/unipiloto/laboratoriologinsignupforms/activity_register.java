package co.edu.unipiloto.laboratoriologinsignupforms;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import co.edu.unipiloto.laboratoriologinsignupforms.db.DbHelper;
import co.edu.unipiloto.laboratoriologinsignupforms.db.DbUsuarios;

public class activity_register extends AppCompatActivity {

    private TextInputLayout nameLayout, usernameLayout, emailLayout, addressLayout, passwordLayout, confirmPasswordLayout, birthdateLayout;
    private TextInputEditText nameInput, usernameInput, emailInput, addressInput, passwordInput, confirmPasswordInput, birthdateInput;
    private TextView mtextExit;
    private Spinner userTypeSpinner;
    private RadioGroup genderGroup;
    private RadioButton radioMasc, radioFem, radioBi;
    private Button registerButton;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        DbUsuarios dbUsuarios = new DbUsuarios(activity_register.this);
        // Inicializar el SDK de Places
        Places.initialize(getApplicationContext(), "AIzaSyAMp_LEifcWuwwHbWa99j8IMUN6MjFj_SQ");

        // Referencia al campo de dirección
        addressInput = findViewById(R.id.addressInput);

        addressInput.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                    .build(activity_register.this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        });

        // Inicialización de los campos
        nameLayout = findViewById(R.id.nameLayout);
        usernameLayout = findViewById(R.id.usernameLayout);
        emailLayout = findViewById(R.id.emailLayout);
        addressLayout = findViewById(R.id.addressLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);
        birthdateLayout = findViewById(R.id.birthdateLayout);
        mtextExit = findViewById(R.id.textExit); //salir a login


        nameInput = (TextInputEditText) nameLayout.getEditText();
        usernameInput = (TextInputEditText) usernameLayout.getEditText();
        emailInput = (TextInputEditText) emailLayout.getEditText();
        addressInput = (TextInputEditText) addressLayout.getEditText();
        passwordInput = (TextInputEditText) passwordLayout.getEditText();
        confirmPasswordInput = (TextInputEditText) confirmPasswordLayout.getEditText();
        birthdateInput = (TextInputEditText) birthdateLayout.getEditText();



        // Spinner para tipo de usuario
        userTypeSpinner = findViewById(R.id.userTypeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.user_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userTypeSpinner.setAdapter(adapter);

        genderGroup = findViewById(R.id.genderGroup);
        radioMasc = findViewById(R.id.radioMasc);
        radioFem = findViewById(R.id.radioFem);
        radioBi = findViewById(R.id.radioBi);

        // Botón de registro
        registerButton = findViewById(R.id.button);
        registerButton.setOnClickListener(view -> {

            String name = nameInput.getText().toString();
            String username = usernameInput.getText().toString();
            String email = emailInput.getText().toString();
            String address = addressInput.getText().toString();
            String password = passwordInput.getText().toString();
            String confirmPassword = confirmPasswordInput.getText().toString();
            String birthdate = birthdateInput.getText().toString();
            String userType = userTypeSpinner.getSelectedItem().toString();


            String gender = "";
            if (radioMasc.isChecked()) {
                gender = "Masculino";
            } else if (radioFem.isChecked()) {
                gender = "Femenino";
            } else if (radioBi.isChecked()) {
                gender = "Binario";
            }

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(username) || TextUtils.isEmpty(email) ||
                    TextUtils.isEmpty(address) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword) || TextUtils.isEmpty(birthdate)) {
                Toast.makeText(activity_register.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(activity_register.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(gender)) {
                Toast.makeText(activity_register.this, "Por favor, seleccione un género", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isUserAdult(birthdate)) {
                Toast.makeText(activity_register.this, "Debe tener al menos 18 años para registrarse", Toast.LENGTH_SHORT).show();
                return;
            }
            // Verificar si el usuario ya está registrado por correo o nombre de usuario

            if (dbUsuarios.isUserRegistered(username, email)) {
                Toast.makeText(activity_register.this, "El usuario o el correo ya están registrados", Toast.LENGTH_SHORT).show();
                return;
            }
            // Insertar en la base de datos
            long id = dbUsuarios.insertarUsuarios(name, username, email, address, birthdate, password, userType, gender);

            if (id > 0) {
                Toast.makeText(activity_register.this, "REGISTRO GUARDADO CON ÉXITO :)", Toast.LENGTH_LONG).show();
                limpiar();
            } else {
                Toast.makeText(activity_register.this, "NO SE GUARDÓ EL REGISTRO :(", Toast.LENGTH_LONG).show();
            }
        });

        // Configurar el mapa
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(googleMap -> {
                mMap = googleMap;

                // Configurar el mapa para permitir colocar un marcador y actualizar la dirección
                mMap.setOnMapClickListener(latLng -> {
                    mMap.clear(); // Limpiar el mapa de cualquier marcador previo
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Ubicación Seleccionada"));
                    addressInput.setText(latLng.latitude + ", " + latLng.longitude); // Actualizar el campo de dirección con las coordenadas
                });

                // Configuración inicial del mapa (opcional)
                LatLng initialPosition = new LatLng(-34, 151); // Posición inicial, ajusta según tu necesidad
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialPosition, 10));
            });
        }

        mtextExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity_register.this, activity_login.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            String address = place.getAddress();
            LatLng latLng = place.getLatLng();
            addressInput.setText(address);

            // Actualizar el mapa con la ubicación seleccionada
            if (latLng != null) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title("Ubicación Seleccionada"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(this, "Error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void limpiar() {
        nameInput.setText("");
        usernameInput.setText("");
        emailInput.setText("");
        addressInput.setText("");
        passwordInput.setText("");
        confirmPasswordInput.setText("");
        birthdateInput.setText("");
        userTypeSpinner.setSelection(0);
        genderGroup.clearCheck();
        if (mMap != null) {
            mMap.clear();
        }
    }

    private boolean isUserAdult(String birthdate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date birthDate = dateFormat.parse(birthdate);
            Calendar birthCalendar = Calendar.getInstance();
            birthCalendar.setTime(birthDate);

            Calendar today = Calendar.getInstance();
            int age = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR);
            if (today.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }
            return age >= 18;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}
