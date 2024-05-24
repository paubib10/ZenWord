package com.example.zenworld;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zenworld.R.id;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity {
    // Variables de instancia
    private TextView textViewPalabra;
    private List<Character> letrasCirculo = new ArrayList<>();
    private int[] btnIdsLetra = {R.id.button1,R.id.button2,R.id.button3,R.id.button4,
            R.id.button5,R.id.button6,R.id.button7};
    private ConstraintLayout constraintLayout;
    private int heightDisplay, widthDisplay;
    private TextView[] CtextViews;
    private List<TextView[]> textViewsList = new ArrayList<>();
    private int colorIndex = 0;
    private String [] palabrasTemp = {"BOL","PALA","COCHE","MUSICA","REVISTA"};
    private int randomIndex;

    Map<String, String> catalogoPalabras = new HashMap<>();
    Map<Integer, Set<String>> catalogoLongitudes = new HashMap<>();
    Map<Integer, Set<String>> catalogoSoluciones = new HashMap<>();
    Map<Integer, String> catalogoPalabrasOcultas = new TreeMap<>();
    Map<String, Integer> catalogoSolucionesEncontradas = new HashMap<>();
    Map<Character, Integer> catalogoLetrasDisponibles = new HashMap<>();
    private String palabraIntroducida;
    private int bonus = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        leerArchivo();
        inicializarVariables();
        configurarBotones();
        configurarPalabrasOcultas();
    }

    private void inicializarVariables() {
        textViewPalabra = findViewById(R.id.textView1);
        constraintLayout = findViewById(R.id.constraintLayout);

        // ASIGNAR LETRAS A BOTONES
        List<String> palabras = new ArrayList<>(catalogoPalabras.values());
        Random random = new Random();
        String palabraAleatoria = obtenerPalabraAleatoria();
        System.out.println("Palabra aleatoria: " + palabraAleatoria);
        obtenerCatalogoSoluciones(palabraAleatoria);
        seleccionarPalabrasOcultas(palabraAleatoria);
        asignarLetrasABotones(palabraAleatoria.toUpperCase());

        if (catalogoSoluciones.isEmpty()) {
            System.out.println("El catálogo de soluciones está vacío.");
            return;
        } else {
            for (Map.Entry<Integer, Set<String>> entry : catalogoSoluciones.entrySet()) {
                int longitud = entry.getKey();
                Set<String> soluciones = entry.getValue();

                System.out.println("Longitud: " + longitud);
                System.out.println("Soluciones:");

                for (String solucion : soluciones) {
                    System.out.println(solucion);
                }

                System.out.println("------");
            }
        }

        if (catalogoPalabrasOcultas.isEmpty()) {
            System.out.println("El catálogo de soluciones está vacío.");
            return;
        } else {
            for (Map.Entry<Integer, String> entry : catalogoPalabrasOcultas.entrySet()) {
                int longitud = entry.getKey();
                String soluciones = entry.getValue();

                System.out.println("Solucion: " + longitud + ":");
                System.out.println(soluciones);
                System.out.println("------");
            }
        }
        palabraIntroducida = " ";
        // PALABRAS OCULTAS CUADRADITOS
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        heightDisplay = displayMetrics.heightPixels;
        widthDisplay = displayMetrics.widthPixels;
    }

    private void configurarBotones() {
        configurarBotonClear();
        configurarRandom();
        configurarBotonBonus();
        configurarBotonAyuda();
        configurarBotonReiniciar();
        configurarBotonSend();
    }

    private void configurarBotonClear() {
        Button btnClear = findViewById(R.id.button9);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearPalabra(view);
            }
        });
    }

    private void configurarRandom() {
        ImageButton btnRandom = findViewById(R.id.imageButton1);
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

    private void configurarBotonAyuda() {
        ImageButton btnAyuda = findViewById(id.imageButton4);
        btnAyuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomIndex = new Random().nextInt(palabrasTemp.length);
                String palabraAleatoria = palabrasTemp[randomIndex];

                // Mostrar una palabra de la lista de palabras
                mostraPrimeraLletra(palabraAleatoria, randomIndex);
            }
        });
    }

    private void configurarBotonReiniciar() {
        ImageButton btnReiniciar = findViewById(id.imageButton3);
        btnReiniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reiniciarJuego();
            }
        });
    }

    private void configurarBotonSend() {
        Button sendButton = findViewById(R.id.button10);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                palabraIntroducida = textViewPalabra.getText().toString();

                boolean esSolucion = false;
                for (Map.Entry<Integer, String> entry : catalogoPalabrasOcultas.entrySet()) {
                    Integer key = entry.getKey();
                    String value = entry.getValue();

                    if (esPalabraSolucio(value, palabraIntroducida.toLowerCase())) {
                        catalogoSolucionesEncontradas.put(palabraIntroducida, Color.BLACK);
                        mostraMissatge("Has descobert una paraula amagada", false);
                        muestraPalabra(palabraIntroducida, key);

                        // Eliminar la palabra del catalogo de palabras ocultas
                        catalogoPalabrasOcultas.remove(key);
                        esSolucion = true;
                        break;
                    }
                }

                // Si la palabra introducida no es una de las palabras ocultas pero es una solución posible
                if(!esSolucion && catalogoPalabras.containsValue(palabraIntroducida.toLowerCase())){
                    // Verificar si la palabra ya ha sido introducida
                    if (!catalogoSolucionesEncontradas.containsKey(palabraIntroducida)) {
                        catalogoSolucionesEncontradas.put(palabraIntroducida, Color.BLACK);
                        mostraMissatge("Paraula vàlida! Tens un bonus", false);

                        bonus++;
                    } else { // La palabra ya ha sido introducida
                        mostraMissatge("Aquesta ja la tens", false);
                        // Cambiamos el color de la palabra repetida a rojo
                        catalogoSolucionesEncontradas.put(palabraIntroducida, Color.RED);
                    }
                } else if (!esSolucion) {
                    mostraMissatge("Paraula no vàlida!", false);
                }

                // Ya se han encontrado todas las palabras, catalagoPalabrasOcultas está vacío
                if (catalogoPalabrasOcultas.isEmpty()) {
                    mostraMissatge("Has guanyat! Enhorabona", false);
                    disableViews(constraintLayout.getId());
                }

                textViewPalabra.setText("");
                for (int btnId : btnIdsLetra) {
                    Button button = findViewById(btnId);
                    button.setEnabled(true);
                    button.setTextColor(Color.WHITE);
                }
            }
        });
    }

    private void configurarBotonBonus() {
        ImageButton btnBonus = findViewById(id.imageButton2);
        btnBonus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bonus >= 5) {
                    // Seleccionar una palabra oculta no completada de forma aleatoria
                    List<Integer> indicesNoCompletados = new ArrayList<>();
                    for (Map.Entry<Integer, String> entry : catalogoPalabrasOcultas.entrySet()) {
                        if (!catalogoSolucionesEncontradas.containsKey(entry.getValue())) {
                            indicesNoCompletados.add(entry.getKey());
                        }
                    }
                    if (!indicesNoCompletados.isEmpty()) {
                        int randomIndex = new Random().nextInt(indicesNoCompletados.size());
                        int indiceAleatorioNoCompletado = indicesNoCompletados.get(randomIndex);
                        String palabraAleatoria = catalogoPalabrasOcultas.get(indiceAleatorioNoCompletado);

                        // Mostrar la primera letra de la palabra oculta
                        mostraPrimeraLletra(palabraAleatoria.toUpperCase(), indiceAleatorioNoCompletado);

                        // Restar 5 bonus
                        bonus -= 5;
                    } else {
                        // No hay palabras ocultas para mostrar la primera letra
                    }
                } else {
                    mostrarVentanaEmergeneteBonus();
                }
            }
        });
    }

    private void configurarPalabrasOcultas() {
        int[] guias = {R.id.guideline1, R.id.guideline2, R.id.guideline3, R.id.guideline4, R.id.guideline5};

        for (int i = 0; i < guias.length; i++) {
            String palabraOculta = catalogoPalabrasOcultas.get(i);
            if (palabraOculta != null) {
                int numLetras = palabraOculta.length();
                CtextViews = crearFilaTextViewsGood(guias[i], numLetras);
                for (TextView textView : CtextViews) {
                    textView.setBackgroundColor(Color.BLUE);
                }
                textViewsList.add(CtextViews); // Agregar el array de TextViews a la lista
            }
        }
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

    private void randomCircle() {
        if(letrasCirculo.size() == btnIdsLetra.length) {
            Collections.shuffle(letrasCirculo);
            for (int i = 0; i < btnIdsLetra.length; i++) {
                Button button = findViewById(btnIdsLetra[i]);
                button.setText(String.valueOf(letrasCirculo.get(i)));
            }
        } else {
            Log.e("Error", "No hay suficientes letras en el círculo");
        }
    }

    public void mostrarVentanaEmergeneteBonus() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Encertades (0 de 10): \n");

        StringBuilder message = new StringBuilder();
        for(Map.Entry<String, Integer> entry : catalogoSolucionesEncontradas.entrySet()) {
            String palabra = entry.getKey();
            int color = entry.getValue();

            // Si el color es rojo, añadir la palabra en rojo al mensaje
            if(color == Color.RED) {
                message.append("<font color='#FF0000'>").append(palabra).append("</font><br>");
            } else {
                message.append(palabra).append("<br>");
            }
        }

        builder.setMessage(Html.fromHtml(message.toString()));
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public TextView[] crearFilaTextViewsGood(int guia, int lletres) {
        TextView[] textViews = new TextView[lletres];

        // Establecer un tamaño fijo para cada TextView
        int width = 125; // Reemplaza FIXED_SIZE con el tamaño que quieres para tus TextViews

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
            textView.setTextSize(30);
            textView.setBackgroundColor(Color.BLUE);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setTypeface(null, Typeface.BOLD);

            // Añadir TextView al LinearLayout
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
            params.setMargins(5, 10, 5, 10);
            textView.setLayoutParams(params);
            linearLayout.addView(textView);
            textViews[i] = textView;
        }

        return textViews;
    }

    public static boolean esPalabraSolucio(String palabra1, String palabra2) {
        Map<Character, Integer> letrasDisponibles = construirCatalogo(palabra1);

        // Recorrer las letras de la palabra2
        for (char letra : palabra2.toCharArray()) {
            // Verificar si la letra está disponible en la palabra1
            if (!letrasDisponibles.containsKey(letra) || letrasDisponibles.get(letra) == 0) {
                return false; // La letra no está disponible en la palabra1 o se ha agotado
            }
            letrasDisponibles.put(letra, letrasDisponibles.get(letra) - 1);
        }
        // Verificamos que las palabras sean iguales (letras en la misma posición)
        return palabra1.equals(palabra2);
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

    private void mostraPrimeraLletra(String s, int posicio){
        // Obtener el array de TextViews correspondiente a la posición dada
        TextView[] textViews = obtenerTextViewsPorPosicion(posicio);

        // Verificar si la longitud de la palabra es menor o igual al número de TextViews disponibles
        if (s.length() <= textViews.length) {
            // Verificar si la posición ya está ocupada
            if (textViews[0].getText().toString().isEmpty()) {
                // Si no está ocupada, mostrar la letra en el TextView correspondiente
                textViews[0].setTextColor(Color.WHITE);
                textViews[0].setText(String.valueOf(s.charAt(0)));
            } else {
                // Si está ocupada, buscar otra línea donde no esté ocupada
                for (int i = 1; i < textViews.length; i++) {
                    if (textViews[i].getText().toString().isEmpty()) {
                        textViews[i].setTextColor(Color.WHITE);
                        textViews[i].setText(String.valueOf(s.charAt(0)));
                        break;
                    }
                }
            }
        } else {
            // Si la longitud de la palabra es mayor que el número de TextViews disponibles, mostrar un mensaje de error
            Log.e("Error", "La longitud de la palabra es mayor que el número de TextViews disponibles");
        }
    }

    private void reiniciarJuego() {
        int[] colorCasilla = {Color.GREEN, Color.MAGENTA, Color.YELLOW, Color.CYAN, Color.RED, Color.BLUE};
        int[] circleResources = {R.drawable.green_circle, R.drawable.purple_circle, R.drawable.yellow_circle,
                R.drawable.blue_circle, R.drawable.red_circle, R.drawable.orange_circle};

        // Limpiar el TextView de la palabra
        textViewPalabra.setText("");

        // Habilitar los botones de letras y hacerlos visibles
        for (int btnId : btnIdsLetra) {
            Button button = findViewById(btnId);
            button.setEnabled(true);
            button.setVisibility(View.VISIBLE);
            button.setTextColor(Color.WHITE);
        }

        // Limpiar los TextViews de las palabras ocultas
        for (TextView[] textViews : textViewsList) {
            for (TextView textView : textViews) {
                textView.setText("");
                textView.setBackgroundColor(Color.BLUE); // Restablecer el color de fondo a azul
                textView.setBackgroundColor(colorCasilla[colorIndex]);
            }
        }

        ImageView imageViewCircle = findViewById(R.id.imageView);
        imageViewCircle.setImageResource(circleResources[colorIndex]);

        colorIndex = (colorIndex + 1) % colorCasilla.length;

        // Limpiar los catálogos
        catalogoPalabras.clear();
        catalogoLongitudes.clear();
        catalogoSoluciones.clear();
        catalogoPalabrasOcultas.clear();
        catalogoSolucionesEncontradas.clear();
        catalogoLetrasDisponibles.clear();

        // Restablecer la variable bonus a 0
        bonus = 0;

        // Restablecer el estado del juego
        inicializarVariables();
        configurarPalabrasOcultas();
    }

    private void reiniciarJuego2() {
        int[] colorCasilla = {Color.GREEN, Color.MAGENTA, Color.YELLOW, Color.CYAN, Color.RED, Color.BLUE};
        int[] circleResources = {R.drawable.green_circle, R.drawable.purple_circle, R.drawable.yellow_circle,
                R.drawable.blue_circle, R.drawable.red_circle, R.drawable.orange_circle};

        // Limpiar el TextView de la palabra
        textViewPalabra.setText("");

        // Habilitar los botones de letras y hacerlos visibles
        for (int btnId : btnIdsLetra) {
            Button button = findViewById(btnId);
            button.setEnabled(true);
            button.setVisibility(View.VISIBLE);
            button.setTextColor(Color.WHITE);
        }

        // Desordenar las letras del círculo
        randomCircle();

        // Asignar letras a los botones
        asignarLetrasABotones("CASTAÑA");

        // Limpiar los TextViews de las palabras ocultas
        for (TextView[] textViews : textViewsList) {
            for (TextView textView : textViews) {
                textView.setText("");
                textView.setBackgroundColor(colorCasilla[colorIndex]);
            }
        }

        ImageView imageViewCircle = findViewById(R.id.imageView);
        imageViewCircle.setImageResource(circleResources[colorIndex]);

        colorIndex = (colorIndex + 1) % colorCasilla.length;
    }

    private void enableViews(int parent) {
        ViewGroup viewGroup = (ViewGroup) findViewById(parent);
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child.getId() != R.id.imageButton2 && child.getId() != R.id.imageButton3) {
                child.setEnabled(true);
            }
        }
    }

    private void disableViews(int parent) {
        ViewGroup viewGroup = (ViewGroup) findViewById(parent);
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child.getId() != R.id.imageButton2 && child.getId() != R.id.imageButton3) {
                child.setEnabled(false);
            }
        }
    }

    private void mostraMissatge ( String s , boolean llarg ){
        Context context = getApplicationContext () ;
        CharSequence text = s;
        int duration = Toast . LENGTH_LONG ;
        if(!llarg){
            duration = Toast. LENGTH_SHORT ;
        }

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void leerArchivo() {
        try {
            InputStream is = getResources().openRawResource(R.raw.paraules2);
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            String line;
            while((line = r.readLine()) != null) {
                //System.out.println(line);
                //catalogoPalabras.put(line, line);
                agregarPalabra(line);
            }

        } catch (Exception e) {
            // Manejar la excepción
            e.printStackTrace();
        }
    }

    public void agregarPalabra(String line) {
        String[] partes = line.split(";");
        if (partes.length == 2) {
            String palabraConAcentos = partes[0].trim();
            String palabraSinAcentos = partes[1].trim();

            // Agregar la palabra con acentos y su versión sin acentos al catálogo
            catalogoPalabras.put(palabraConAcentos, palabraSinAcentos);
            //System.out.println(catalogoPalabras.get(palabra));

            // Agregar la palabra al catálogo de palabras por longitud
            int longitud = palabraConAcentos.length();

            // Si la clave no existe creamos una nueva entrada
            if (!catalogoLongitudes.containsKey(longitud)) {
                catalogoLongitudes.put(longitud, new HashSet<>());
            }
            catalogoLongitudes.get(longitud).add(palabraConAcentos);
            catalogoLongitudes.get(longitud).add(palabraSinAcentos);
        }
    }

    // Método para obtener una palabra aleatoria de forma aleatoria
    private String obtenerPalabraAleatoria() {
        // Obtener el máximo número de letras entre las palabras en el catálogo
        int maximoNumeroLetras = Collections.max(catalogoLongitudes.keySet());

        // Generar un número aleatorio entre 3 y el máximo número de letras
        int longitudAleatoria = new Random().nextInt(3) + 4; //preguntar si debe ser entre 3 y 7 o como esta
        System.out.println("Longitud aleatorio de letra "+ longitudAleatoria);

        // Obtener el conjunto de palabras de la longitud aleatoria
        Set<String> palabrasLongitudAleatoria = catalogoLongitudes.get(longitudAleatoria);

        // Si no hay palabras de esa longitud, regresar null
        while (palabrasLongitudAleatoria == null || palabrasLongitudAleatoria.isEmpty()) {
            System.out.println("No se ha encontrado palabras con esta longitud, seguimos buscando ...");
            longitudAleatoria = new Random().nextInt(3) + 4; //preguntar si debe ser entre 3 y 7 o como esta
            System.out.println("Longitud aleatorio de letra "+ longitudAleatoria);

            // Obtener el conjunto de palabras de la longitud aleatoria
            palabrasLongitudAleatoria = catalogoLongitudes.get(longitudAleatoria);
        }

        // Convertir el conjunto a una lista para poder seleccionar una palabra aleatoria
        List<String> listaPalabrasLongitudAleatoria = new ArrayList<>(palabrasLongitudAleatoria);
        // Elegir una palabra aleatoria de ese conjunto
        String palabraAleatoria = listaPalabrasLongitudAleatoria.get(new Random().nextInt(listaPalabrasLongitudAleatoria.size()));
        catalogoPalabrasOcultas.put(4, palabraAleatoria);
        return palabraAleatoria;
    }

    // Método para seleccionar otras cuatro palabras
    private void obtenerCatalogoSoluciones(String palabraAleatoria) {
        Set<Character> letrasPalabraAleatoria = new HashSet<>();
        for (char letra : palabraAleatoria.toCharArray()) {
            letrasPalabraAleatoria.add(letra);
        }
        int longitud = palabraAleatoria.length();

        for (int i = 1; i < 5; i++) {
            int j = 1;
            if(longitud - i < 3){
                break;
            }
            Set<String> palabrasLongitud = catalogoLongitudes.get(longitud - i);
            while (palabrasLongitud == null || palabrasLongitud.isEmpty()) {
                palabrasLongitud = catalogoLongitudes.get(longitud - i - j);
                j++;
            }

            // Encontrar una palabra con letras presentes en la palabra aleatoria
            for (String palabra : palabrasLongitud) {
                for (char letra : palabraAleatoria.toCharArray()) {
                    letrasPalabraAleatoria.add(letra);
                }

                // Verificar si todas las letras de la palabra están contenidas en las letras únicas de la palabra aleatoria
                boolean contieneTodasLasLetras = true;
                for (char letra : palabra.toCharArray()) {
                    if (!letrasPalabraAleatoria.contains(letra)) {
                        contieneTodasLasLetras = false;
                        break;
                    }
                    letrasPalabraAleatoria.remove(letra);
                }

                // Si la palabra cumple con la condición, agregarla a la lista de otras palabras
                if (contieneTodasLasLetras) {
                    if (!catalogoSoluciones.containsKey(palabra.length())) {
                        catalogoSoluciones.put(palabra.length(), new TreeSet<>());
                    }
                    catalogoSoluciones.get(palabra.length()).add(palabra);

                }
            }
        }

    }

    private void seleccionarPalabrasOcultas(String palabraAleataroria) {
        int j = 1;
        int longitud = palabraAleataroria.length();
        for (int k = 0; k < 5; k++){
            Set<String> palabrasSoluciones = catalogoSoluciones.get(longitud - j);
            int loopCount = 0;
            while ((palabrasSoluciones == null || palabrasSoluciones.isEmpty()) && (longitud - j) > 3 && loopCount < 10) {
                palabrasSoluciones = catalogoSoluciones.get(longitud - j);
                j++;
                loopCount++;
            }
            if (palabrasSoluciones != null && !palabrasSoluciones.isEmpty()) {
                List<String> listaSoluciones = new ArrayList<>(palabrasSoluciones);
                String solucionAleatoria = listaSoluciones.get(new Random().nextInt(listaSoluciones.size()));
                // Check if the word has already been selected
                while (catalogoPalabrasOcultas.containsValue(solucionAleatoria)) {
                    // If the word has already been selected, select a new word
                    solucionAleatoria = listaSoluciones.get(new Random().nextInt(listaSoluciones.size()));
                }
                catalogoPalabrasOcultas.put(k, solucionAleatoria);
            }
            j++;
        }

        // Check if a word has been selected for every index
        for (int k = 0; k < 5; k++) {
            if (!catalogoPalabrasOcultas.containsKey(k)) {
                // If not, select a word of any length
                for (Set<String> palabrasSoluciones : catalogoSoluciones.values()) {
                    if (!palabrasSoluciones.isEmpty()) {
                        List<String> listaSoluciones = new ArrayList<>(palabrasSoluciones);
                        String solucionAleatoria = listaSoluciones.get(new Random().nextInt(listaSoluciones.size()));
                        // Check if the word has already been selected
                        while (catalogoPalabrasOcultas.containsValue(solucionAleatoria)) {
                            // If the word has already been selected, select a new word
                            solucionAleatoria = listaSoluciones.get(new Random().nextInt(listaSoluciones.size()));
                        }
                        catalogoPalabrasOcultas.put(k, solucionAleatoria);
                        break;
                    }
                }
            }
        }
        sortCatalogoPalabrasOcultas();
    }

    private void sortCatalogoPalabrasOcultas() {
        // Crear una lista para almacenar los valores del mapa
        List<String> listaDeValores = new ArrayList<>(catalogoPalabrasOcultas.values());

        // Ordenar la lista
        Collections.sort(listaDeValores, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                int comparacionPorLongitud = Integer.compare(s1.length(), s2.length());
                if (comparacionPorLongitud == 0) {
                    return s1.compareTo(s2);
                } else {
                    return comparacionPorLongitud;
                }
            }
        });

        // Asegurarse de que no excedemos el tamaño de la lista
        int max = Math.min(4, listaDeValores.size());
        for (int i = 0; i < max; i++) {
            catalogoPalabrasOcultas.put(i, listaDeValores.get(i));
        }
    }
}