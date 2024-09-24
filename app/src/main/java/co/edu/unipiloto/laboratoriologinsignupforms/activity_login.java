package co.edu.unipiloto.laboratoriologinsignupforms;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import co.edu.unipiloto.laboratoriologinsignupforms.db.DbUsuarios;

public class activity_login extends AppCompatActivity {

    private EditText mUsername, mPassword;
    private Button mbuttonInicio, mbuttonRegistrar;
    private DbUsuarios dbusuarios;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        mUsername = findViewById(R.id.login_usuario);
        mPassword = findViewById(R.id.login_contraseña);
        mbuttonInicio = findViewById(R.id.btnInicio);
        mbuttonRegistrar = findViewById(R.id.btnRegistro);

        dbusuarios = new DbUsuarios(this);

        mbuttonInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();

                if (dbusuarios.checkLogin(username, password)) {
                    Intent intent = new Intent(activity_login.this, Homesesion.class);
                    intent.putExtra("USERNAME", username);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(activity_login.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                }

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(activity_login.this, "Por favor, complete todos los campos para iniciar sesion", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });



        mbuttonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity_login.this, activity_register.class);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}