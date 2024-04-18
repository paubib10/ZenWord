package com.example.zenworld;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.zenworld.R.id;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView textViewPalabra;
    private List<Character> letrasCirculo = new ArrayList<>();
    private int[] btnIdsLetra = {R.id.button1,R.id.button2,R.id.button3,R.id.button4,
            R.id.button5,R.id.button6,R.id.button7};
    private String [] pa = {"Pala, Palo, Pelo"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewPalabra = findViewById(R.id.textView1);

        // BOTONES LETRAS DEL CÍRCULO
        for (int btnId : btnIdsLetra) {
            configurarBtnLetra(btnId);
        }

        // BOTÓN CLEAR
        Button btnClear = findViewById(R.id.button9);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearPalabra(view);
            }
        });

        // BOTÓN RANDOM
        configurarRandom();

        ImageButton btnBonus = findViewById(id.imageButton2);
        btnBonus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarVentanaEmergeneteBonus();
            }
        });

    }
    public void configurarBtnLetra(int btnId) {
        Button btn = findViewById(btnId);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLetra(view);
            }
        });
    }

    private void setLetra(View view) {
        Button btn = (Button) view;
        String letra = btn.getText().toString();

        // Agregar letra
        String palabraActual = textViewPalabra.getText().toString();
        palabraActual += letra;
        textViewPalabra.setText(palabraActual);

        // Desacticar boton y cambiar color gris
        btn.setEnabled(false);
        btn.setTextColor(Color.GRAY);
    }

    public void clearPalabra(View view) {
        textViewPalabra.setText("");

        for (int btnId : btnIdsLetra) {
            Button button = findViewById(btnId);
            button.setEnabled(true);
            button.setTextColor(Color.WHITE);
        }
    }

    public void configurarRandom() {
        ImageButton btnRandom = findViewById(R.id.imageButton1);

        // OnClickListener del botón RANDOM
        btnRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomCircle();
            }
        });

        // Recorremos los botones de letras en el círculo
        for(int i = 0; i < btnIdsLetra.length; i++) {
            Button btn = findViewById(btnIdsLetra[i]); // get id
            char letra = btn.getText().charAt(0); // get letter on circle
            // Añadimos la letra en el circulo si no existe en la lista
            if (!letrasCirculo.contains(letra)) {
                letrasCirculo.add(letra);
            }
        }
    }

    /**
     * Método que desordena las letras del círculo
     * y las asigna a los botones de letras.
     */
    private void randomCircle() {
        Collections.shuffle(letrasCirculo);
        for (int i = 0; i < btnIdsLetra.length; i++) {
            Button button = findViewById(btnIdsLetra[i]);
            button.setText(String.valueOf(letrasCirculo.get(i)));
        }
    }

    /**
     * Método que muestra una ventana emergente con las palabras
     * y aciertos conseguidos.
     */
    public void mostrarVentanaEmergeneteBonus() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Encertades (0 de 10): \n");

        String message = "";
        for(int i = 0; i < pa.length; i++) {
            message = pa[i];
        }
        
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public TextView[] crearFilaTextViews(int guia, int lletres) {
        return null;
    }

    public void esPalabraSolucion(String paraula1, String paraula2) {

    }
}