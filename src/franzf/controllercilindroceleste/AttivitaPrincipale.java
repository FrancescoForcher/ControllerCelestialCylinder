package franzf.controllercilindroceleste;


import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import at.abraxas.amarino.Amarino;

public class AttivitaPrincipale extends Activity implements OnSeekBarChangeListener {

	private static final String DEVICE_ADDRESS = "00:13:EF:00:07:AE";
	final int DELAY = 50;
	long lastChange = 0;

	SeekBar SeekBarAngolo;
	SeekBar SeekBarLed;
	View segnaleConnessione;
	ProgressBar progBarConn;
	TextView AngoloAttuale;
	int angoloRealeStep;
	float angoloRealeGradi;
	ControllerAngolareCustomView CerchioController;
	//AngoloStep serve a qualcosa?
	//int angoloStep;
	int ledVal;
	/*
	 * mode, cioè come impostare l'angolo:
	 * 1=Antiorario (il cilindro girerà sempre in senso antiorario)
	 * 2=Orario (il cilindro girerà sempre in senso orario)
	 * 3=Misto (il cilindro girerà in senso orario o antiorario, a seconda di cosa è più breve)
	 * 4=Limitato (il cilindro girerà mantenendo l'angolo tra 0° e 360°)
	 */
	int mode=1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attivita_principale);


		//Trova le varie view
		SeekBarAngolo = (SeekBar) findViewById(R.id.SeekBarAngoloID);
		SeekBarLed = (SeekBar) findViewById(R.id.SeekBarLed);
		CerchioController = (ControllerAngolareCustomView) findViewById(R.id.controllerAngolareCustomView1);
		segnaleConnessione = findViewById(R.id.segnaleConnessione);
		progBarConn = (ProgressBar) findViewById(R.id.progressBarConnessione);
		AngoloAttuale = (TextView) findViewById(R.id.textViewAngoloAttuale);
		

		SeekBarAngolo.setOnSeekBarChangeListener(this);
		SeekBarLed.setOnSeekBarChangeListener(this);
	}

	@Override
	protected void onStart() {
		super.onStart();

		// load last state
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		angoloRealeStep = prefs.getInt("angolopref", 0);
		ledVal = prefs.getInt("ledpref", 0);

		SeekBarAngolo.setProgress(angoloRealeStep);
		SeekBarLed.setProgress(ledVal);

		//this.registerReceiver(at.abraxas.amarino.AmarinoIntent.);
		Amarino.connect(this, DEVICE_ADDRESS);
		segnaleConnessione.setBackgroundColor(Color.GREEN);
		progBarConn.setVisibility(View.INVISIBLE);

		//if()

	}

	@Override
	protected void onStop() {
		super.onStop();
		// save state
		PreferenceManager.getDefaultSharedPreferences(this)
		.edit()
			.putInt("angolopref", angoloRealeStep)
			.putInt("ledpref", ledVal)
		.commit();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// stop Amarino's background service, we don't need it any more 
		Amarino.disconnect(this, DEVICE_ADDRESS);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_attivita_principale, menu);
		return true;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		// do not send to many updates, Arduino can't handle so much
		if (System.currentTimeMillis() - lastChange > DELAY ){
			updateState(seekBar);
			lastChange = System.currentTimeMillis();
		}
	}

	private void updateState(SeekBar seekBar) {

		switch (seekBar.getId()){
		case R.id.SeekBarAngoloID:
			//Imposta le varie variablili che rappresentano in vari modi l'angolo
			settaAngoli(seekBar.getProgress());
			
			Amarino.sendDataToArduino(this, DEVICE_ADDRESS, 'a', angoloRealeStep);	
			break;
		case R.id.SeekBarLed:
			//angoloRealeStep = SeekBarAngolo.getProgress();
			//angoloStep = (int)Math.round(map(angoloRealeStep,0,66,0,48));
			ledVal = seekBar.getProgress();
			Amarino.sendDataToArduino(this, DEVICE_ADDRESS, 'l', ledVal);	
			break;
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		lastChange = System.currentTimeMillis();
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		updateState(seekBar);
	}

	
	//Imposta i vari angoli in giro per l'app
	void settaAngoli(int AngStep) {
		angoloRealeStep = AngStep;
		CerchioController.AngoloCerchioStep = AngStep;
		angoloRealeGradi=angoloRealeStep*5.625f;
		AngoloAttuale.setText("Angolo Attuale: "+angoloRealeGradi);
		CerchioController.invalidate();
	}
	
	public void riceviAngolo(int angStep){
		
	};
	
	
		
	/*
	 * Bottoni
	 */
	public void onPiu(View view){
		//Lo zero non serve a niente
		Amarino.sendDataToArduino(this, DEVICE_ADDRESS, 'p',0);
		angoloRealeStep=angoloRealeStep+1;
		SeekBarAngolo.setProgress(angoloRealeStep);
	}

	public void onMeno(View view){
		//Lo zero non serve a niente
		Amarino.sendDataToArduino(this, DEVICE_ADDRESS, 'm',0);
		angoloRealeStep=angoloRealeStep-1;
		SeekBarAngolo.setProgress(angoloRealeStep);
	}

	public void onReset(View view){
		//Lo zero non serve a niente
		Amarino.sendDataToArduino(this, DEVICE_ADDRESS, 'r',0);
		settaAngoli(0);
		SeekBarAngolo.setProgress(angoloRealeStep);
	}
	
	
	
	//La funzione map
	public static final double map(double x, double in_min, double in_max, double out_min, double out_max)
	{
		return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}
}
