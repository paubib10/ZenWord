package com.example.zenworld;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
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
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

// Blanca Atiénzar Martínez
// Pau Toni Bibiloni Martínez
// Hai Zi Bibiloni Trobat

/**
 * Usamos el hash en vez de estructuras de datos no ordenadas porque es más eficiente. Las tablas hash permiten tiempos
 * de acceso, inserción y eliminación constantes (O(1)), mientras que las estructuras no ordenadas requieren recorrer
 * todos los elementos (O(n)). Esto hace que el hash sea ideal para un acceso rápido y frecuente a los datos.
 */

public class MainActivity extends AppCompatActivity {
    // Variables de instancia
    private TextView textViewPalabra;
    private TextView textViewInformacion;
    private TextView textViewBonus;
    private List<Character> letrasCirculo = new ArrayList<>();
    private int[] btnIdsLetra = {R.id.button1,R.id.button2,R.id.button3,R.id.button4,
            R.id.button5,R.id.button6,R.id.button7};
    private ConstraintLayout constraintLayout;
    private int heightDisplay, widthDisplay;
    private TextView[] CtextViews;
    private List<TextView[]> textViewsList = new ArrayList<>();
    private int colorIndex = 0;
    Map<String, String> catalogoPalabras = new HashMap<>();
    Map<Integer, Set<String>> catalogoLongitudes = new HashMap<>();
    Map<Integer, Set<String>> catalogoSoluciones = new HashMap<>();
    Map<Integer, String> catalogoPalabrasOcultas = new TreeMap<>();
    Map<String, Integer> catalogoSolucionesEncontradas = new HashMap<>();
    private String palabraIntroducida;
    private int bonus = 0;
    private int paraulesEncertades = 0;
    private int paraulesPosiblesSolucions = 0;
    private int contadorLetras = 0;

    @Override
    /**
     * Método onCreate que se ejecuta al iniciar la actividad.
     * @param savedInstanceState El estado de la instancia guardada.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        leerArchivo();
        inicializarVariables();
        configurarBotones();
        configurarPalabrasOcultas();
    }

    /**
     * Método para inicializar las variables del juego.
     */
    private void inicializarVariables() {
        // Se asigna cada variable con el elemento correspondiente del juego
        textViewPalabra = findViewById(R.id.textView1);
        constraintLayout = findViewById(R.id.constraintLayout);
        textViewInformacion = findViewById(R.id.textView2);
        textViewBonus = findViewById(R.id.textView3);
        paraulesEncertades = 0;
        paraulesPosiblesSolucions = 0;
        bonus = 0;

        // Configuración de la partida, se elije la palabra que se pondra en el circulo,
        // se añaden las posibles soluciones al catalogoSoluciones y se seleccionan las palabras ocultas
        String palabraAleatoria = obtenerPalabraAleatoria();
        esSolucio(palabraAleatoria, catalogoPalabras.values().iterator()); // Añadir posibles soluciones al catalogoSoluciones
        seleccionarPalabrasOcultas(palabraAleatoria);
        // Se asignan las letras a los botones del circulo
        asignarLetrasABotones(palabraAleatoria.toUpperCase());

        // Actualizar el texto del textViewBonus
        textViewBonus.setText(String.valueOf(bonus));

        // ASIGNAR TEXTO INFORMATIVO
        textViewInformacion.setText("Encertades (" + paraulesEncertades + " de " + paraulesPosiblesSolucions +"): \n");

        // Mostrar el catálogo de soluciones y palabras ocultas
        if (catalogoSoluciones.isEmpty()) {
            System.out.println("El catálogo de soluciones está vacío.");
            return;
        } else {
            // Success
        }

        // Mostrar el catálogo de palabras ocultas
        if (catalogoPalabrasOcultas.isEmpty()) {
            System.out.println("El catálogo de palabras ocultas está vacío.");
            return;
        } else {
            System.out.println(catalogoPalabrasOcultas);
        }
        palabraIntroducida = " ";

        // Configurar el tamaño de la pantalla
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        heightDisplay = displayMetrics.heightPixels;
        widthDisplay = displayMetrics.widthPixels;
    }

    /**
     * Método para configurar los botones de la interfaz de usuario.
     */
    private void configurarBotones() {
        configurarBotonClear();
        configurarRandom();
        configurarBotonBonus();
        configurarBotonAyuda();
        configurarBotonReiniciar();
        configurarBotonSend();
    }

    /**
     * Configura el botón de limpiar para borrar el contenido del TextView que muestra la palabra.
     */
    private void configurarBotonClear() {
        Button btnClear = findViewById(R.id.button9);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearPalabra(view);
            }
        });
    }

    /**
     * Configura el botón de letras aleatorias para mezclar las letras en el círculo.
     */
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

    /**
     * Configura el botón de ayuda para mostrar la primera letra de una palabra oculta.
     */
    private void configurarBotonAyuda() {
        ImageButton btnAyuda = findViewById(id.imageButton4);
        btnAyuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bonus >= 5) { // Verificar si hay suficientes bonus
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

                        // Actualizar el texto del textViewBonus
                        textViewBonus.setText(String.valueOf(bonus));
                    } else {
                        // No hay palabras ocultas para mostrar la primera letra
                    }
                }
            }
        });
    }

    /**
     * Configura el botón de reiniciar para reiniciar la partida.
     */
    private void configurarBotonReiniciar() {
        ImageButton btnReiniciar = findViewById(id.imageButton3);
        btnReiniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reiniciarJuego();
            }
        });
    }

    /**
     * Configura el botón de enviar para verificar si la palabra introducida por el usuario es una solución.
     */
    private void configurarBotonSend() {
        Button sendButton = findViewById(R.id.button10);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener la palabra introducida por el usuario
                palabraIntroducida = textViewPalabra.getText().toString();

                boolean esSolucion = false;
                Iterator<Map.Entry<Integer, String>> iterator = catalogoPalabrasOcultas.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Integer, String> entry = iterator.next();
                    Integer key = entry.getKey();
                    String value = entry.getValue();

                    // Verificar si la palabra introducida es una de las palabras ocultas
                    if (verificadorPalabra(value, palabraIntroducida.toLowerCase())) {
                        catalogoSolucionesEncontradas.put(palabraIntroducida, Color.BLACK);
                        mostraMissatge("Has descobert una paraula amagada", false);
                        muestraPalabra(palabraIntroducida, key);
                        paraulesEncertades++;

                        // Eliminar la palabra del catalogo de palabras ocultas
                        iterator.remove();
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
                        paraulesEncertades++;

                        bonus++;

                        textViewBonus.setText(String.valueOf(bonus));
                    } else { // La palabra ya ha sido introducida
                        mostraMissatge("Aquesta ja la tens", false);
                        // Cambiamos el color de la palabra repetida a rojo
                        catalogoSolucionesEncontradas.put(palabraIntroducida, Color.RED);
                    }
                } else if (!esSolucion) {
                    mostraMissatge("Paraula no vàlida!", false);
                }

                // Actualizar el texto del textViewInformacion
                textViewInformacion.setText("Encertades (" + paraulesEncertades + " de " + paraulesPosiblesSolucions +"): ");
                StringBuilder message = new StringBuilder();
                Iterator<Map.Entry<String, Integer>> iterator1 = catalogoSolucionesEncontradas.entrySet().iterator();
                while (iterator1.hasNext()) {
                    Map.Entry<String, Integer> entry = iterator1.next();
                    String palabra = entry.getKey();
                    int color = entry.getValue();

                    message.append(palabra + ", ");
                }

                // Ya se han encontrado todas las palabras, catalagoPalabrasOcultas está vacío
                if (catalogoPalabrasOcultas.isEmpty()) {
                    mostraMissatge("Has guanyat! Enhorabona", false);
                    message.append(".");
                    disableViews(constraintLayout.getId());
                }

                textViewInformacion.append(Html.fromHtml(message.toString()));
                textViewPalabra.setText("");
                // Restablecer los botones de letras
                for (int btnId : btnIdsLetra) {
                    Button button = findViewById(btnId);
                    button.setEnabled(true);
                    button.setTextColor(Color.WHITE);
                }
            }
        });
    }

    /**
     * Configura el botón de bonus para mostrar una ventana emergente con un resumen de las palabras acertadas.
     */
    private void configurarBotonBonus() {
        ImageButton btnBonus = findViewById(id.imageButton2);
        btnBonus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarVentanaEmergeneteBonus();
            }
        });
    }

    /**
     * Configura las palabras ocultas en el juego
     */
    private void configurarPalabrasOcultas() {
        // Crear filas de TextViews para cada palabra en el catálogo de palabras ocultas
        int[] guias = {R.id.guideline1, R.id.guideline2, R.id.guideline3, R.id.guideline4, R.id.guideline5};

        // Recorrer las guías y las palabras ocultas
        for (int i = 0; i < guias.length; i++) {
            // Obtener la palabra oculta en la posición i
            String palabraOculta = catalogoPalabrasOcultas.get(i);
            if (palabraOculta != null) {
                int numLetras = palabraOculta.length();
                CtextViews = crearFilaTextViewsGood(guias[i], numLetras);
                // Establecer un tamaño fijo para cada TextView
                for (TextView textView : CtextViews) {

                    GradientDrawable gradientDrawable = new GradientDrawable();
                    gradientDrawable.setShape(GradientDrawable.RECTANGLE);
                    gradientDrawable.setCornerRadius(10);
                    gradientDrawable.setStroke(2, Color.TRANSPARENT);
                    gradientDrawable.setColor(Color.CYAN);

                    // Establecer el GradientDrawable como fondo del TextView
                    textView.setBackground(gradientDrawable);

                    //textView.setBackgroundColor(Color.CYAN );
                }
                textViewsList.add(CtextViews); // Agregar el array de TextViews a la lista
            }
        }
    }

    /**
     * Asigna letras a los botones del círculo y configura un OnClickListener para cada botón.
     *
     * @param palabra La palabra que se va a asignar a los botones.
     */
    private void asignarLetrasABotones(String palabra) {
        // Dividir la palabra en letras
        char[] letras = palabra.toCharArray();

        letrasCirculo.clear();

        // Recorrer los botones de letras en el círculo
        for (int i = 0; i < btnIdsLetra.length; i++) {
            Button button = findViewById(btnIdsLetra[i]);
            // Verificar si hay letras disponibles
            if (i < letras.length) {
                // Asignar la letra al botón
                button.setText(String.valueOf(letras[i]));

                letrasCirculo.add(letras[i]);
                contadorLetras++;

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

    /**
     * Borra el contenido del TextView que muestra la palabra y habilita todos los botones de letras.
     * Este método se ejecuta cuando se llama a la acción asociada con la vista.
     *
     * @param view botón clear.
     */
    public void clearPalabra(View view) {
        textViewPalabra.setText("");

        // Itera sobre todos los IDs de los botones de letras.
        List<Integer> btnIdsList = new ArrayList<>();
        for (int id : btnIdsLetra) {
            btnIdsList.add(id);
        }
        Iterator<Integer> iterator = btnIdsList.iterator();
        while (iterator.hasNext()) {
            int btnId = iterator.next();
            Button button = findViewById(btnId);
            button.setEnabled(true);
            button.setTextColor(Color.WHITE);
        }
    }

    private void randomCircle() {
        // Mezcla aleatoriamente los elementos en 'letrasCirculo'.
        Collections.shuffle(letrasCirculo);

        for (int i = 0; i < btnIdsLetra.length; i++) {
            Button button = findViewById(btnIdsLetra[i]);
            if (i < letrasCirculo.size()) {
                char letra = letrasCirculo.get(i);
                // Verificar si 'letra' es una letra válida
                if (Character.isLetter(letra)) {
                    // Asignar la letra al botón
                    button.setText(String.valueOf(letra));
                    // Habilitar el botón y hacerlo visible
                    button.setEnabled(true);
                    button.setVisibility(View.VISIBLE);
                } else {
                    // Si 'letra' no es una letra válida, desactivar el botón y hacerlo invisible
                    button.setEnabled(false);
                    button.setVisibility(View.INVISIBLE);
                }
            } else {
                // Si no hay más letras, desactivar el botón y hacerlo invisible
                button.setEnabled(false);
                button.setVisibility(View.INVISIBLE);
                button.setText("");
            }
        }
    }

    /**
     * Muestra una ventana emergente (AlertDialog) con un resumen de las palabras acertadas.
     * La ventana incluye el número de palabras acertadas y posibles, y una lista de palabras
     * con algunas resaltadas en rojo.
     */
    public void mostrarVentanaEmergeneteBonus() {
        // Crea un nuevo AlertDialog.Builder con el contexto actual.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Asigna el título al AlertDialog
        builder.setTitle("Encertades (" + paraulesEncertades + " de " + paraulesPosiblesSolucions +"): \n");

        // Construye el mensaje con las palabras acertadas.
        StringBuilder message = new StringBuilder();
        for (Iterator<Map.Entry<String, Integer>> it = catalogoSolucionesEncontradas.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, Integer> entry = it.next();
            String palabra = entry.getKey();
            int color = entry.getValue();

            // Si el color es rojo, añadir la palabra en rojo al mensaje
            if(color == Color.RED) {
                message.append("<font color='red'>").append(palabra.toLowerCase()).append("</font><br>");
            } else {
                message.append(palabra.toLowerCase()).append(", ");
            }
        }

        // Establece el mensaje del AlertDialog con las palabras formateadas en HTML.
        builder.setMessage(Html.fromHtml(message.toString()));
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();

        //builder.setPositiveButton("OK", null);
        //AlertDialog dialog = builder.create();
        //dialog.show();
    }

    /**
     * Crea una fila de TextViews dentro de un LinearLayout y los agrega a un ConstraintLayout.
     * Cada TextView se configura con un tamaño fijo, color de fondo azul, texto centrado y en negrita.
     *
     * @param guia El ID del View que se usará como guía para las restricciones de posición del LinearLayout.
     * @param lletres El número de TextViews a crear.
     * @return Un array de TextViews creados.
     */
    public TextView[] crearFilaTextViewsGood(int guia, int lletres) {
        // Crear un array de TextViews con el tamaño especificado.
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

        // Crear y configurar cada TextView.
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

    /**
     * Verifica si todas las letras de 'palabra2' están disponibles en 'palabra1' en las mismas cantidades.
     * También verifica que ambas palabras sean iguales.
     *
     * @param palabra1 La palabra de referencia que contiene las letras disponibles.
     * @param palabra2 La palabra que se va a verificar.
     * @return true si todas las letras de 'palabra2' están disponibles en 'palabra1' y ambas palabras son iguales; false en caso contrario.
     */
    public static boolean verificadorPalabra(String palabra1, String palabra2) {
        // Construir un catálogo de letras disponibles en 'palabra1'
        Map<Character, Integer> letrasDisponibles = construirCatalogo(palabra1);

        // Recorrer las letras de la palabra2
        char[] palabra2Array = palabra2.toCharArray();
        List<Character> palabra2List = new ArrayList<>();
        for (int i = 0; i < palabra2Array.length; i++) {
            palabra2List.add(palabra2Array[i]);
        }
        Iterator<Character> iterator = palabra2List.iterator();
        while (iterator.hasNext()) {
            char letra = iterator.next();
            // Verificar si la letra está disponible en la palabra1
            if (!letrasDisponibles.containsKey(letra) || letrasDisponibles.get(letra) == 0) {
                return false; // La letra no está disponible en la palabra1 o se ha agotado
            }

            // Decrementar la cantidad de la letra disponible.
            letrasDisponibles.put(letra, letrasDisponibles.get(letra) - 1);
        }

        // Verificamos que las palabras sean iguales (letras en la misma posición)
        return palabra1.equals(palabra2);
    }

    /**
     * Construye un catálogo (mapa) de letras y sus cantidades a partir de una palabra.
     * Este método cuenta la frecuencia de cada letra en la palabra dada.
     *
     * @param palabra La palabra de la que se va a construir el catálogo.
     * @return Un mapa que contiene cada letra y su cantidad correspondiente en la palabra.
     */
    private static Map<Character, Integer> construirCatalogo(String palabra) {
        Map<Character, Integer> catalogo = new HashMap<>();
        char[] palabraArray = palabra.toCharArray();
        List<Character> palabraList = new ArrayList<>();
        for (int i = 0; i < palabraArray.length; i++) {
            palabraList.add(palabraArray[i]);
        }
        Iterator<Character> iterator = palabraList.iterator();
        while (iterator.hasNext()) {
            char letra = iterator.next();
            catalogo.put(letra, catalogo.getOrDefault(letra, 0) + 1);
        }
        return catalogo;
    }

    /**
     * Muestra una palabra en un conjunto de TextViews en una posición específica.
     *
     * @param s La palabra que se va a mostrar.
     * @param posicion La posición en la que se encuentran los TextViews correspondientes.
     */
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

    /**
     * Obtiene el array de TextViews correspondiente a una posición específica.
     *
     * @param posicion La posición para la cual se deben obtener los TextViews.
     * @return Un array de TextViews en la posición especificada.
     */
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

    /**
     * Muestra la primera letra de una palabra en un conjunto de TextViews en una posición específica.
     * Si el TextView correspondiente a la primera posición ya está ocupado, busca otro TextView disponible
     * para mostrar la letra.
     *
     * @param s La palabra de la cual se va a mostrar la primera letra.
     * @param posicio La posición en la que se encuentran los TextViews correspondientes.
     */
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

    /**
     * Reinicia el juego, restableciendo las variables y creando nuevas filas de TextViews para cada palabra en el nuevo catálogo.
     *
     * El método también elimina los TextViews existentes, limpia las listas de palabras y soluciones, y actualiza los colores de las casillas y el círculo.
     */
    private void reiniciarJuego() {
        // Colores y recursos de círculo
        int[] colorCasilla = {Color.GREEN, Color.MAGENTA, Color.YELLOW, Color.CYAN, Color.RED, Color.BLUE};
        int[] circleResources = {R.drawable.green_circle, R.drawable.purple_circle, R.drawable.yellow_circle,
                R.drawable.cyan_circle, R.drawable.red_circle, R.drawable.blue_circle};

        enableViews(constraintLayout.getId());

        // Eliminar todos los TextViews existentes
        for (Iterator<TextView[]> it = textViewsList.iterator(); it.hasNext(); ) {
            TextView[] textViews = it.next();
            List<TextView> textViewList = Arrays.asList(textViews);
            Iterator<TextView> textViewIterator = textViewList.iterator();
            while (textViewIterator.hasNext()) {
                TextView textView = textViewIterator.next();
                ((ViewGroup) textView.getParent()).removeView(textView); // Eliminar el TextView del layout
            }
        }
        textViewsList = new ArrayList<>();// Limpiar la lista de TextViews

        colorIndex = (colorIndex + 1) % colorCasilla.length; // Actualizar el índice de color

        // Generar un nuevo catálogo de palabras para la partida
        catalogoPalabras = new HashMap<>();
        catalogoLongitudes = new HashMap<>();
        catalogoSoluciones = new HashMap<>();
        catalogoPalabrasOcultas = new TreeMap<>();
        catalogoSolucionesEncontradas = new HashMap<>();

        leerArchivo();
        inicializarVariables();

        // Crear nuevas filas de TextViews para cada palabra en el nuevo catálogo
        int[] guias = {R.id.guideline1, R.id.guideline2, R.id.guideline3, R.id.guideline4, R.id.guideline5};
        for (int i = 0; i < guias.length; i++) {
            String palabraOculta = catalogoPalabrasOcultas.get(i);
            if (palabraOculta != null) {
                int numLetras = palabraOculta.length();
                TextView[] textViews = crearFilaTextViewsGood(guias[i], numLetras);
                for (TextView textView : textViews) {

                    GradientDrawable gradientDrawable = new GradientDrawable();
                    gradientDrawable.setShape(GradientDrawable.RECTANGLE);
                    gradientDrawable.setCornerRadius(10);
                    gradientDrawable.setStroke(2, Color.TRANSPARENT);
                    gradientDrawable.setColor(colorCasilla[colorIndex]);

                    // Establecer el GradientDrawable como fondo del TextView
                    textView.setBackground(gradientDrawable);

                    //textView.setBackgroundColor(colorCasilla[colorIndex]);
                    textView.setText(""); // Vaciar las casillas

                    // Cambiar el color de las casillas y el círculo
                    ImageView imageViewCircle = findViewById(R.id.imageView);
                    imageViewCircle.setImageResource(circleResources[colorIndex]);
                }
                textViewsList.add(textViews); // Agregar el array de TextViews a la lista
            }
        }
    }

    /**
     * Habilita todas las vistas hijos de un ViewGroup, excepto los botones de imagen específicos.
     *
     * @param parent El ID del ViewGroup que contiene las vistas a habilitar.
     */
    private void enableViews(int parent) {
        ViewGroup viewGroup = (ViewGroup) findViewById(parent);
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);

            // Habilitar la vista si no es uno de los botones de imagen específicos
            if (child.getId() != R.id.imageButton2 && child.getId() != R.id.imageButton3) {
                child.setEnabled(true);
            }
        }
    }

    /**
     * Deshabilita todas las vistas hijos de un ViewGroup, excepto los botones de imagen específicos.
     *
     * @param parent El ID del ViewGroup que contiene las vistas a deshabilitar.
     */
    private void disableViews(int parent) {
        ViewGroup viewGroup = (ViewGroup) findViewById(parent);
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);

            // Deshabilitar la vista si no es uno de los botones de imagen específicos
            if (child.getId() != R.id.imageButton2 && child.getId() != R.id.imageButton3) {
                child.setEnabled(false);
            }
        }
    }

    /**
     * Muestra un mensaje Toast con la cadena de texto especificada.
     *
     * @param s La cadena de texto que se mostrará en el mensaje Toast.
     * @param llarg Indica si la duración del mensaje Toast será larga (true) o corta (false).
     */
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

    /**
     * Lee un archivo de recursos de texto y agrega cada línea como una palabra al juego.
     */
    private void leerArchivo() {
        try {
            InputStream is = getResources().openRawResource(R.raw.paraules2);
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            String line;
            while((line = r.readLine()) != null) {
                agregarPalabra(line);
            }

        } catch (Exception e) {
            // Manejar la excepción
            e.printStackTrace();
        }
    }

    /**
     * Agrega una palabra al catálogo de palabras y al catálogo de palabras por longitud.
     *
     * @param line La línea del archivo que contiene la palabra en formato "palabraConAcentos;palabraSinAcentos".
     */
    public void agregarPalabra(String line) {
        String[] partes = line.split(";");
        if (partes.length == 2) {
            String palabraConAcentos = partes[0].trim();
            String palabraSinAcentos = partes[1].trim();

            // Agregar la palabra con acentos y su versión sin acentos al catálogo
            catalogoPalabras.put(palabraConAcentos, palabraSinAcentos);

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

    /**
     * Obtiene una palabra aleatoria del catálogo de palabras por longitud.
     *
     * @return Una palabra aleatoria del catálogo.
     */
    private String obtenerPalabraAleatoria() {
        // Obtener la longitud máxima de las palabras
        Collections.max(catalogoLongitudes.keySet());

        // Generar un número aleatorio entre 3 y el máximo número de letras
        int longitudAleatoria = new Random().nextInt(3) + 4;
        System.out.println("Longitud aleatorio de letraS "+ longitudAleatoria);

        // Obtener el conjunto de palabras de la longitud aleatoria
        Set<String> palabrasLongitudAleatoria = catalogoLongitudes.get(longitudAleatoria);

        // Si no hay palabras de esa longitud, regresar null
        while (palabrasLongitudAleatoria == null || palabrasLongitudAleatoria.isEmpty()) {
            System.out.println("No se ha encontrado palabras con esta longitud, seguimos buscando ...");
            longitudAleatoria = new Random().nextInt(3) + 4;
            System.out.println("Longitud aleatorio de letra "+ longitudAleatoria);

            // Obtener el conjunto de palabras de la longitud aleatoria
            palabrasLongitudAleatoria = catalogoLongitudes.get(longitudAleatoria);
        }

        // Convertir el conjunto a una lista para poder seleccionar una palabra aleatoria
        List<String> listaPalabrasLongitudAleatoria = new ArrayList<>(palabrasLongitudAleatoria);
        // Elegir una palabra aleatoria de ese conjunto
        String palabraAleatoria = listaPalabrasLongitudAleatoria.get(new Random().nextInt(listaPalabrasLongitudAleatoria.size()));

        // Agregar la palabra con acentos al catálogo de soluciones
        Iterator<Map.Entry<String, String>> iterator = catalogoPalabras.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            if (entry.getKey().equals(palabraAleatoria)) {
                if (!catalogoSoluciones.containsKey(entry.getValue().length())) {
                    catalogoSoluciones.put(entry.getValue().length(), new TreeSet<>());
                }
                catalogoSoluciones.get(entry.getValue().length()).add(entry.getValue());
            }
        }
        return palabraAleatoria;
    }

    /**
     * Verifica si una palabra aleatoria puede ser una solución para el juego, comparándola con el catálogo de palabras.
     *
     * @param palabraAleatoria La palabra aleatoria seleccionada para el juego.
     * @param catalogoIterator El iterador sobre el catálogo de palabras.
     */
    private void esSolucio(String palabraAleatoria, Iterator<String> catalogoIterator) {
        // Crear un mapa de letras únicas de la palabra aleatoria y sus recuentos
        Map<Character, Integer> letrasPalabraAleatoria = new HashMap<>();
        char[] palabraAleatoriaArray = palabraAleatoria.toCharArray();
        List<Character> palabraAleatoriaList = new ArrayList<>();
        for (int i = 0; i < palabraAleatoriaArray.length; i++) {
            palabraAleatoriaList.add(palabraAleatoriaArray[i]);
        }
        Iterator<Character> iterator = palabraAleatoriaList.iterator();
        while (iterator.hasNext()) {
            char letra = iterator.next();
            letrasPalabraAleatoria.put(letra, letrasPalabraAleatoria.getOrDefault(letra, 0) + 1);
        }

        while (catalogoIterator.hasNext()) {
            String palabra = catalogoIterator.next();
            // Verificar si la longitud de la palabra está entre 3 y 7
            if (palabra.length() < 3 || palabra.length() > 7) {
                continue;
            }

            // Crear una copia de las letras únicas de la palabra aleatoria y sus recuentos
            Map<Character, Integer> letrasRestantes = new HashMap<>(letrasPalabraAleatoria);
            boolean contieneTodasLasLetras = true;
            char[] palabraArray = palabra.toCharArray();
            List<Character> palabraList = new ArrayList<>();
            for (int i = 0; i < palabraArray.length; i++) {
                palabraList.add(palabraArray[i]);
            }
            Iterator<Character> iterator1 = palabraList.iterator();
            while (iterator1.hasNext()) {
                char letra = iterator1.next();
                if (!letrasRestantes.containsKey(letra) || letrasRestantes.get(letra) == 0) {
                    contieneTodasLasLetras = false;
                    break;
                }
                letrasRestantes.put(letra, letrasRestantes.get(letra) - 1);
            }
            if (contieneTodasLasLetras) {
                if (!catalogoSoluciones.containsKey(palabra.length())) {
                    catalogoSoluciones.put(palabra.length(), new TreeSet<>());
                }
                catalogoSoluciones.get(palabra.length()).add(palabra);
                paraulesPosiblesSolucions++;
            }
        }
    }

    /**
     * Selecciona las palabras ocultas para el juego, incluyendo la palabra aleatoria y otras soluciones.
     *
     * @param palabraAleataroria La palabra aleatoria seleccionada para el juego.
     */
    private void seleccionarPalabrasOcultas(String palabraAleataroria) {
        int longitud = palabraAleataroria.length();
        Set<String> todasLasSoluciones = new HashSet<>();

        // Agregar todas las soluciones posibles al conjunto todasLasSoluciones
        for (int i = 1; i < 5; i++) {
            if(longitud - i < 3){
                break;
            }
            Set<String> palabrasSoluciones = catalogoSoluciones.get(longitud - i);
            if (palabrasSoluciones != null && !palabrasSoluciones.isEmpty()) {
                todasLasSoluciones.addAll(palabrasSoluciones);
            }
        }

        // Convertir el conjunto todasLasSoluciones a una lista para poder seleccionar palabras de manera aleatoria
        List<String> listaTodasLasSoluciones = new ArrayList<>(todasLasSoluciones);

        // Seleccionar aleatoriamente hasta 4 soluciones de listaTodasLasSoluciones
        Random rand = new Random();
        int contadorSoluciones = 0;
        while (contadorSoluciones < 4 && !listaTodasLasSoluciones.isEmpty()) {
            int indiceAleatorio = rand.nextInt(listaTodasLasSoluciones.size());
            String solucionAleatoria = listaTodasLasSoluciones.get(indiceAleatorio);

            // Verificar si la solución ya está en el catálogo de palabras ocultas o es la palabra aleatoria
            if (!catalogoPalabrasOcultas.containsValue(solucionAleatoria) && !solucionAleatoria.equals(palabraAleataroria)) {
                listaTodasLasSoluciones.remove(indiceAleatorio); // Eliminar la solución seleccionada de listaTodasLasSoluciones
                catalogoPalabrasOcultas.put(contadorSoluciones, solucionAleatoria);
                contadorSoluciones++;
            } else {
                // Si la solución ya está en el catálogo de palabras ocultas o es la palabra aleatoria, eliminarla de listaTodasLasSoluciones y no agregarla al catálogo
                listaTodasLasSoluciones.remove(indiceAleatorio);
            }
        }

        // Añadir la palabra aleatoria en la última posición del catálogo de palabras ocultas
        catalogoPalabrasOcultas.put(contadorSoluciones, palabraAleataroria);

        sortCatalogoPalabrasOcultas();
    }

    /**
     * Ordena el catálogo de palabras ocultas por longitud y alfabéticamente.
     */
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