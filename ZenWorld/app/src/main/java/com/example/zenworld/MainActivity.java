package com.example.zenworld;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.zenworld.R.id;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private TextView textViewPalabra;
    private List<Character> letrasCirculo = new ArrayList<>();
    private int[] btnIdsLetra = {R.id.button1,R.id.button2,R.id.button3,R.id.button4,
            R.id.button5,R.id.button6,R.id.button7};
    private ConstraintLayout constraintLayout;
    private int heightDisplay, widthDisplay;
    private String [] pa = {"Pala, Palo, Pelo"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewPalabra = findViewById(R.id.textView1);
        constraintLayout = findViewById(R.id.constraintLayout);

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

        // BOTÓN BONUS
        ImageButton btnBonus = findViewById(id.imageButton2);
        btnBonus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarVentanaEmergeneteBonus();
            }
        });

        // PALABRAS OCULTAS CUADRADITOS
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        heightDisplay = displayMetrics.heightPixels;
        widthDisplay = displayMetrics.widthPixels;

        int[] guias = {R.id.guideline1, R.id.guideline2, R.id.guideline3, R.id.guideline4, R.id.guideline5};
        int[] numLetras = {5,4,3,4,3};

        for (int i = 0; i < guias.length; i++) {
            TextView[] textViews = crearFilaTextViewss(guias[i], numLetras[i]);
            for (TextView textView : textViews) {
                // Configuramos el color de fondo para cada TextView
                textView.setText("??????");
                textView.setBackgroundColor(Color.BLUE);
            }
        }
    }

    public TextView[] crearFilaTextViewss(int guia, int lletres) {
        TextView[] textViews = new TextView[lletres];

        // Calcular el ancho de cada TextView
        int width = widthDisplay / lletres;

        TextView previousTextView = null;

        for (int i = 0; i < lletres; i++) {
            TextView textView = new TextView(this);
            textView.setId(View.generateViewId());
            textView.setText("");
            textView.setTextSize(20);
            textView.setBackgroundColor(Color.BLUE);

            // Añadir TextView al ConstraintLayout
            constraintLayout.addView(textView);
            textViews[i] = textView;

            // Configurar restricciones
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(width, width);
            textView.setLayoutParams(params);

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            int id = textView.getId();
            
            // Si no es el primer TextView, conectar al TextView anterior en la fila
            if (previousTextView != null) {
                constraintSet.connect(id, ConstraintSet.START, previousTextView.getId(), ConstraintSet.END, 0);
            } else {
                // Si es el primer TextView, conectar a la guía
                constraintSet.connect(id, ConstraintSet.START, guia, ConstraintSet.START, 0);
            }

            constraintSet.connect(id, ConstraintSet.TOP, guia, ConstraintSet.TOP, 0);
            constraintSet.applyTo(constraintLayout);

            previousTextView = textView; // Actualizar el TextView anterior
        }

        return textViews;
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

    /**
     * Método que añade la letra del botón al TextView de la palabra.
     * @param view Botón pulsado
     */
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
        ConstraintSet constraintSet = new ConstraintSet();
        TextView[] textViews = new TextView[lletres];

        for (int i = 0; i < lletres; i++) {
            TextView textView = new TextView(this);
            textView.setId(View.generateViewId());
            textView.setText("");
            textView.setTextSize(20);

            // Añadir TextView al ConstraintLayout
            constraintLayout.addView(textView);
            textViews[i] = textView;
        }

        constraintSet.clone(constraintLayout);
        for (int i = 0; i < lletres; i++) {
            TextView textView = textViews[i];
            int id = textView.getId();

            // Conectar TextViews al guideline
            constraintSet.connect(id, ConstraintSet.START, guia, ConstraintSet.START, 0);
            constraintSet.connect(id, ConstraintSet.END, guia, ConstraintSet.END, 0);

            // Calcular los márgenes superiores e inferiores
            int topMargin = heightDisplay / 10; // Margen superior
            int bottomMargin = heightDisplay / 20; // Margen inferior
            constraintSet.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, topMargin * (i + 1));
            constraintSet.connect(id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, bottomMargin * (lletres - i));

            // Establecer anchura y altura de los TextView
            constraintSet.constrainWidth(id, ConstraintSet.WRAP_CONTENT);
            constraintSet.constrainHeight(id, ConstraintSet.WRAP_CONTENT);
        }
        constraintSet.applyTo(constraintLayout);

        return textViews;
    }

    public static boolean esPalabraSolucio(String palabra1, String palabra2) {
        Map<Character, Integer> letrasDisponibles = construirCatalogo(palabra1);

        // Verificar si podemos construir la palabra2 utilizando las letras disponibles
        for (char letra : palabra2.toCharArray()) {
            if (!letrasDisponibles.containsKey(letra) || letrasDisponibles.get(letra) == 0) {
                return false; // La letra no está disponible en la palabra1 o se ha agotado
            }
            letrasDisponibles.put(letra, letrasDisponibles.get(letra) - 1);
        }
        return true;
    }

    private static Map<Character, Integer> construirCatalogo(String palabra) {
        Map<Character, Integer> catalogo = new HashMap<>();
        for (char letra : palabra.toCharArray()) {
            catalogo.put(letra, catalogo.getOrDefault(letra, 0) + 1);
        }
        return catalogo;
    }
}