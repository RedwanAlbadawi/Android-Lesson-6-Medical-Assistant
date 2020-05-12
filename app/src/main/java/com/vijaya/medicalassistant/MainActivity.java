package com.vijaya.medicalassistant;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // text to speech
    private TextToSpeech textToSpeech;


    // Speech Recognizer
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private TextView mVoiceInputTv;
    private ImageButton mSpeakBtn;


    // shared preferences

    private String extractedName = null;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Name = "name";
    SharedPreferences sharedpreferences;

    // getting time...

    String time = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);


        // initialize texttospeech
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int arg0) {
                if(arg0 == TextToSpeech.SUCCESS)
                {
                    // if it is successfully initialzied then set the speed rate to .8f and set language to US
                    textToSpeech.setLanguage(Locale.US);

                    textToSpeech.setSpeechRate(0.8f);

                    // initially speak message hello

                    textToSpeech.speak("Hello",TextToSpeech.QUEUE_FLUSH,null);
                }
            }
        });



        mVoiceInputTv = (TextView) findViewById(R.id.voiceInput);

        mSpeakBtn = (ImageButton) findViewById(R.id.btnSpeak);

        mSpeakBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startVoiceInput();
            }
        });



    }




















    private void startVoiceInput() {
        // create action recognize intent
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // set the model for the speech recognizer
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        // set the language locale.getDefault will get the android device default language if we don't write this
        // line it will also do the same.
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        // initial messsage
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, How can I help you?");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);


                    // check for message


                    String message = result.get(0).toString();

                    if(message.equals("hello")){
                        textToSpeech.speak("What is your name?",TextToSpeech.QUEUE_FLUSH,null);
                    }
                    else if(message.contains("my name is")){
                        // if message contains string my name is we extract the name from the message and
                        // store it as shared preferences.
                        extractedName = message.substring(message.indexOf("is") + 2);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(Name,extractedName);
                        editor.commit();
                    }

                    // other cases
                    else if(message.contains("i am not feeling well what should i do")
                            || message.equalsIgnoreCase("i am not feeling well what should i do")){
                        textToSpeech.speak("I can understand. Please tell your symptoms in short.",TextToSpeech.QUEUE_FLUSH,null);

                    }
                    else if(message.contains("thank you my medical assistant")
                           || message.equalsIgnoreCase("thank you my medical assistant")){
                        textToSpeech.speak("Thank you "+extractedName,TextToSpeech.QUEUE_FLUSH,null);

                    }
                    else if(message.contains("what time is it")
                            || message.equalsIgnoreCase("what time is it")){
                        textToSpeech.speak("The time is "+time.replace(":"," "),TextToSpeech.QUEUE_FLUSH,null);

                    }
                    else if(message.contains("what medicines should i take")
                            || message.equalsIgnoreCase("what medicines should i take") ||
                            message.contains("what medicine should i take")
                            || message.equalsIgnoreCase("what medicine should i take")){
                        textToSpeech.speak("I think you have a fever. Please take this medicine.",TextToSpeech.QUEUE_FLUSH,null);
                    }

                    mVoiceInputTv.setText(result.get(0));
                }
                break;
            }

        }
    }

}
