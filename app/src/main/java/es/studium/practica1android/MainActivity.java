package es.studium.practica1android;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.os.LocaleListCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Idiomas disponibles, en el mismo orden que el array "idiomas" de strings.xml
    private static final String[] IDIOMAS = {"es", "en", "fr"};
    private static final int MAYORIA_DE_EDAD = 18;

    EditText etNombre;
    EditText etApellidos;
    EditText etEdad;
    RadioGroup rgGenero;
    Spinner spIdioma;
    Spinner spEstadoCivil;
    SwitchCompat swHijos;
    TextView tvMensaje;
    Button btnMostrar;
    Button btnReiniciar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Asociamos los controles JAVA con los controles XML
        etNombre = findViewById(R.id.etNombre);
        etApellidos = findViewById(R.id.etApellidos);
        etEdad = findViewById(R.id.etEdad);
        rgGenero = findViewById(R.id.rgGenero);
        spIdioma = findViewById(R.id.spIdioma);
        spEstadoCivil = findViewById(R.id.spEstadoCivil);
        swHijos = findViewById(R.id.swHijos);
        tvMensaje = findViewById(R.id.tvMensaje);
        btnMostrar = findViewById(R.id.btnMostrar);
        btnReiniciar = findViewById(R.id.btnReiniciar);

        configurarIdioma();

        // Añadimos los Listeners de los botones
        btnMostrar.setOnClickListener(view -> mostrarDatos());
        btnReiniciar.setOnClickListener(view -> reiniciar());
    }

    // Idioma de la aplicación en este momento ("es" por defecto si no es ninguno de los otros)
    private String idiomaActual() {
        String idioma = getResources().getConfiguration().getLocales().get(0).getLanguage();
        for (String disponible : IDIOMAS) {
            if (disponible.equals(idioma)) {
                return disponible;
            }
        }
        return "es";
    }

    // Cambia el idioma por código, sin tocar los ajustes del sistema operativo
    private void configurarIdioma() {
        String actual = idiomaActual();
        for (int i = 0; i < IDIOMAS.length; i++) {
            if (IDIOMAS[i].equals(actual)) {
                spIdioma.setSelection(i);
            }
        }
        spIdioma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String elegido = IDIOMAS[position];
                // Solo se recarga la pantalla si el idioma elegido es distinto del actual
                if (!elegido.equals(idiomaActual())) {
                    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(elegido));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void mostrarDatos() {
        String nombre = etNombre.getText().toString().trim();
        String apellidos = etApellidos.getText().toString().trim();
        String edadTexto = etEdad.getText().toString().trim();

        // Comprobamos qué campos de texto están en blanco
        List<String> faltan = new ArrayList<>();
        if (nombre.isEmpty()) {
            faltan.add(getString(R.string.campo_nombre));
        }
        if (apellidos.isEmpty()) {
            faltan.add(getString(R.string.campo_apellidos));
        }
        if (edadTexto.isEmpty()) {
            faltan.add(getString(R.string.campo_edad));
        }
        if (!faltan.isEmpty()) {
            tvMensaje.setTextColor(ContextCompat.getColor(this, R.color.rojo_error));
            tvMensaje.setText(getString(R.string.falta_escribir, unir(faltan)));
            return;
        }
        tvMensaje.setText("");

        int edad = Integer.parseInt(edadTexto);
        String mayoria = getString(edad >= MAYORIA_DE_EDAD ? R.string.mayor_de_edad : R.string.menor_de_edad);

        String genero;
        int generoElegido = rgGenero.getCheckedRadioButtonId();
        if (generoElegido == R.id.rbHombre) {
            genero = getString(R.string.genero_hombre);
        } else if (generoElegido == R.id.rbMujer) {
            genero = getString(R.string.genero_mujer);
        } else {
            genero = getString(R.string.genero_sin_indicar);
        }

        String estadoCivil = spEstadoCivil.getSelectedItem().toString();
        String hijos = getString(swHijos.isChecked() ? R.string.con_hijos : R.string.sin_hijos);

        String texto = getString(R.string.resultado, apellidos, nombre, mayoria, genero, estadoCivil, hijos);
        Toast.makeText(this, texto, Toast.LENGTH_LONG).show();
    }

    // Devuelve la lista con el formato "a, b y c"
    private String unir(List<String> elementos) {
        if (elementos.size() == 1) {
            return elementos.get(0);
        }
        int ultimo = elementos.size() - 1;
        String principio = TextUtils.join(", ", elementos.subList(0, ultimo));
        return principio + " " + getString(R.string.conjuncion_y) + " " + elementos.get(ultimo);
    }

    // Deja los controles con sus valores iniciales
    private void reiniciar() {
        etNombre.setText("");
        etApellidos.setText("");
        etEdad.setText("");
        rgGenero.clearCheck();
        spEstadoCivil.setSelection(0);
        swHijos.setChecked(false);
        tvMensaje.setText("");
        etNombre.requestFocus();
    }
}
