package edu.wm.mashpotato;

import java.nio.charset.Charset;

import edu.wm.mashpotato.accelerometer.SaveThePotatoActivity;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.nfc.NfcManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class HomeScreenActivity extends Activity implements CreateNdefMessageCallback,
OnNdefPushCompleteCallback {
	NfcAdapter mNfcAdapter;
	NfcManager mNfcManager;
	
	private static final int MESSAGE_SENT = 1;
	
	private ViewFlipper viewFlipper;
	private Button savePotato;
	private float lastX;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_screen);
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		
        // Register callback to set NDEF message
        mNfcAdapter.setNdefPushMessageCallback(this, this);
        // Register callback to listen for message-sent success
        mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
        
        viewFlipper = (ViewFlipper) findViewById(R.id.ViewFlipper01);
        savePotato = (Button) findViewById(R.id.saveThePotato);
        
        savePotato.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				System.out.println("SAVE THE POTATO. DO IT FOR THE CHILDRENNNNNN!!!!");
				Intent intent = new Intent(getApplicationContext(),
						SaveThePotatoActivity.class);
				startActivity(intent);
			}
		});
        
//        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals( getIntent().getAction())) mNfcManager.processIntent(getIntent());
	}
	
	// Method to handle touch event like left to right swap and right to left
		// swap
		public boolean onTouchEvent(MotionEvent touchevent) {
			switch (touchevent.getAction()) {
			// when user first touches the screen to swap
			case MotionEvent.ACTION_DOWN: {
				lastX = touchevent.getX();
				break;
			}
			case MotionEvent.ACTION_UP: {
				float currentX = touchevent.getX();

				// if left to right swipe on screen
				if (lastX < currentX) {
					// If no more View/Child to flip
					if (viewFlipper.getDisplayedChild() == 0)
						break;

					// set the required Animation type to ViewFlipper
					// The Next screen will come in form Left and current Screen
					// will go OUT from Right
					viewFlipper.setInAnimation(this, R.anim.in_from_left);
					viewFlipper.setOutAnimation(this, R.anim.out_to_right);
					// Show the next Screen
					viewFlipper.showNext();
				}

				// if right to left swipe on screen
				if (lastX > currentX) {
					if (viewFlipper.getDisplayedChild() == 1)
						break;
					// set the required Animation type to ViewFlipper
					// The Next screen will come in form Right and current Screen
					// will go OUT from Left
					viewFlipper.setInAnimation(this, R.anim.in_from_right);
					viewFlipper.setOutAnimation(this, R.anim.out_to_left);
					// Show The Previous Screen
					viewFlipper.showPrevious();
				}
				break;
			}
			}
			return false;
		}
	
	@Override
	public void onNdefPushComplete(NfcEvent arg0) {
        // A handler is needed to send messages to the activity when this
        // callback occurs, because it happens from a binder thread
        mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
		
	}
	@Override
	public NdefMessage createNdefMessage(NfcEvent arg0) {
        Time time = new Time();
        time.setToNow();
        String text = ("Beam me up!\n\n" +
                "Beam Time: " + time.format("%H:%M:%S"));
        NdefMessage msg = new NdefMessage(
                new NdefRecord[] { createMimeRecord(
                        "application/edu.wm.mashpotato", text.getBytes())
         /**
          * The Android Application Record (AAR) is commented out. When a device
          * receives a push with an AAR in it, the application specified in the AAR
          * is guaranteed to run. The AAR overrides the tag dispatch system.
          * You can add it back in to guarantee that this
          * activity starts when receiving a beamed message. For now, this code
          * uses the tag dispatch system.
          */
          //,NdefRecord.createApplicationRecord("com.example.android.beam")
        });
        return msg;
	}

    /**
     * Creates a custom MIME type encapsulated in an NDEF record
     *
     * @param mimeType
     */
    public NdefRecord createMimeRecord(String mimeType, byte[] payload) {
        byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
        NdefRecord mimeRecord = new NdefRecord(
                NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload);
        return mimeRecord;
    }
    
    /** This handler receives a message from onNdefPushComplete */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_SENT:
                Toast.makeText(getApplicationContext(), "Potato passed!", Toast.LENGTH_LONG).show();
                break;
            }
        }
    };

}
