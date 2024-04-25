package com.example.zenworld;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

    private TextView[] CtextViews;
    private List<TextView[]> textViewsList = new ArrayList<>();

    private String [] pa = {"Pala, Palo, Pelo"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewPalabra = findViewById(R.id.textView1);
        constraintLayout = findViewById(R.id.constraintLayout);

        // ASIGNAR LETRAS A BOTONES
        asignarLetrasABotones("REVISTA");

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
        int[] numLetras = {3,3,3,4,5};

        for (int i = 0; i < guias.length; i++) {
            CtextViews = crearFilaTextViewsGood(guias[i], numLetras[i]);
            for (TextView textView : CtextViews) {
                // Configuramos el color de fondo para cada TextView
                textView.setBackgroundColor(Color.BLUE);
            }
            textViewsList.add(CtextViews); // Agregar el array de TextViews a la lista
        }

        // BOTON SEND
        Button sendButton = findViewById(R.id.button10);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                muestraPalabra("pal", 0);
                muestraPalabra("sol", 1);
                muestraPalabra("bol", 2);
                muestraPalabra("cola", 3);
                muestraPalabra("chico", 4);
            }
        });
    }

    private void asignarLetrasABotones(String palabra) {
        // Dividir la palabra en letras
        char[] letras = palabra.toCharArray();

        // Recorrer los botones de letras en el círculo
        for (int i = 0; i < btnIdsLetra.length; i++) {
            Button button = findViewById(btnIdsLetra[i]);
            // Verificar si hay letras disponibles
            if (i < letras.length) {
                // Asignar la letra al botón
                button.setText(String.valueOf(letras[i]));
                // Habilitar el botón y hacerlo visible
                button.setEnabled(true);
                button.setVisibility(View.VISIBLE);

                // Establecer OnClickListener para el botón
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Obtener la letra del botón
                        Button clickedButton = (Button) v;
                        String letra = clickedButton.getText().toString();

                        // Actualizar el TextView con la letra
                        textViewPalabra.append(letra);

                        // Cambiar el color del botón y deshabilitarlo
                        clickedButton.setTextColor(Color.GRAY);
                        clickedButton.setEnabled(false);
                    }
                });
            } else {
                // Si no hay más letras, desactivar el botón y hacerlo invisible
                button.setEnabled(false);
                button.setVisibility(View.INVISIBLE);
            }
        }
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

    public TextView[] crearFilaTextViewsGood(int guia, int lletres) {
        TextView[] textViews = new TextView[lletres];

        // Establecer un tamaño fijo para cada TextView
        int width = 150; // Reemplaza FIXED_SIZE con el tamaño que quieres para tus TextViews

        // Crear un LinearLayout para contener los TextViews
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setId(View.generateViewId());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER);
        constraintLayout.addView(linearLayout);

        // Configurar restricciones para el LinearLayout
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.connect(linearLayout.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
        constraintSet.connect(linearLayout.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
        constraintSet.connect(linearLayout.getId(), ConstraintSet.TOP, guia, ConstraintSet.TOP, 0);
        constraintSet.applyTo(constraintLayout);

        for (int i = 0; i < lletres; i++) {
            TextView textView = new TextView(this);
            textView.setId(View.generateViewId());
            textView.setText("");
            textView.setTextSize(20);
            textView.setBackgroundColor(Color.BLUE);

            // Añadir TextView al LinearLayout
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
            params.setMargins(10, 10, 10, 10);
            textView.setLayoutParams(params);
            linearLayout.addView(textView);
            textViews[i] = textView;
        }

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


    private void muestraPalabra(String s, int posicion) {
        // Obtener el array de TextViews correspondiente a la posición dada
        TextView[] textViews = obtenerTextViewsPorPosicion(posicion);
        Log.d("DEBUG", "Mostrando palabra: " + s + " en la posición: " + posicion);

        // Verificar si la longitud de la palabra es menor o igual al número de TextViews disponibles
        if (s.length() <= textViews.length) {
            // Mostrar la palabra en los TextViews correspondientes
            for (int i = 0; i < s.length(); i++) {
                textViews[i].setTextColor(Color.WHITE);
                textViews[i].setText(String.valueOf(s.charAt(i)));
            }
        } else {
            // Si la longitud de la palabra es mayor que el número de TextViews disponibles, mostrar un mensaje de error
            Log.e("Error", "La longitud de la palabra es mayor que el número de TextViews disponibles");
        }
    }

    // Método auxiliar para obtener los TextViews correspondientes a una posición dada
    private TextView[] obtenerTextViewsPorPosicion(int posicion) {
        // Verificar si la posición es válida
        if (posicion >= 0 && posicion < textViewsList.size()) {
            return textViewsList.get(posicion);
        } else {
            // Si la posición no es válida, devolver un array vacío
            Log.e("Error", "Posición no válida");
            return new TextView[0];
        }
    }

}