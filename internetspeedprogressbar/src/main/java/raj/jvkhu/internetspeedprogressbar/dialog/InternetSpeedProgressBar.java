package raj.jvkhu.internetspeedprogressbar.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import raj.jvkhu.internetspeedprogressbar.R;
import raj.jvkhu.internetspeedprogressbar.utilities.SpeedCarrier;

public class InternetSpeedProgressBar {
    private Dialog dialog;
    private ImageView dialog_img_progressbar;
    private TextView dialog_progressbar_title, dialog_progressbar_download_speed, dialog_progressbar_upload_speed;
    private Activity mContext;
    private final String UPLOAD_KEY = "uploadKey";
    private final String DOWNLOAD_KEY = "downloadKey";
    private Handler mainHandler = null;
    private Thread workerThread = null;

    public InternetSpeedProgressBar(Activity context) {
        this.mContext = context;
    }

    public void show() {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        if (dialog == null) {
            dialog = new Dialog(mContext);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_custom_progress_dialog);
            dialog.setCancelable(false);
            dialog_img_progressbar = dialog.findViewById(R.id.dialog_img_progressbar);
            dialog_progressbar_title = dialog.findViewById(R.id.dialog_progressbar_title);
            dialog_progressbar_download_speed = dialog.findViewById(R.id.dialog_progressbar_download_speed);
            dialog_progressbar_upload_speed = dialog.findViewById(R.id.dialog_progressbar_upload_speed);
            dialog_progressbar_title.setText(mContext.getString(R.string.please_wait));
            Glide.with(mContext).asGif()
                    .load(R.drawable.ic_sync_gif)
                    .into(dialog_img_progressbar);
            CalculateInternetSpeed();
        }
        dialog.show();
    }


    public void hide() {
        if (dialog != null && dialog.isShowing()) {
            dialog.hide();
        }
    }

    public void dismiss() {
        if (dialog != null && !mContext.isDestroyed() && !mContext.isFinishing()) {
            dialog.dismiss();
        }
        if (this.workerThread != null) {
            // The thread will end the run method if interruped.
            this.workerThread.interrupt();
            this.workerThread = null;
            Log.v("THREAD", "KILLED");
        }
    }

    private void CalculateInternetSpeed() {
        this.mainHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                Log.v("MESSAGE", "FROM THREAD");
                Bundle data = inputMessage.getData();
                if (data.containsKey(UPLOAD_KEY) && data.containsKey(DOWNLOAD_KEY)) {
                    SpeedCarrier carrier = calculateSpeed(data.getLong(DOWNLOAD_KEY), data.getLong(UPLOAD_KEY));
                    dialog_progressbar_download_speed.setText(String.format("Download: %s %s", carrier.getDlSpeed(), carrier.TypeToString(carrier.getDlType())));
                    dialog_progressbar_upload_speed.setText(String.format("Upload: %s %s", carrier.getUpSpeed(), carrier.TypeToString(carrier.getUpType())));
                }
            }
        };
        this.workerThread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    long totalRxBytesBefore;
                    long totalRxBytesAfter;
                    long totalTxBytesBefore;
                    long totalTxBytesAfter;

                    // TODO here do same for Tx
                    // Get initial value
                    totalRxBytesBefore = TrafficStats.getTotalRxBytes();
                    totalTxBytesBefore = TrafficStats.getTotalTxBytes();
                    // Wait some time
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        return;
                    }
                    totalRxBytesAfter = TrafficStats.getTotalRxBytes();
                    totalTxBytesAfter = TrafficStats.getTotalTxBytes();

                    Message message = mainHandler.obtainMessage();
                    Bundle data = new Bundle();
                    data.putLong(UPLOAD_KEY, totalTxBytesAfter - totalTxBytesBefore);
                    data.putLong(DOWNLOAD_KEY, totalRxBytesAfter - totalRxBytesBefore);
                    message.setData(data);
                    mainHandler.sendMessage(message);
                }
                Log.v("THREAD", "DEAD");
            }
        });
        this.workerThread.start();
    }

    private SpeedCarrier calculateSpeed(long dlDifference, long upDifference) {
        double speed = 0;
        SpeedCarrier carrier = new SpeedCarrier();
        // Calculate up speed
        if (upDifference > 1000000) {
            carrier.setUpType(SpeedCarrier.M_BITS);
            speed = upDifference / 1000000.0;
            carrier.setUpSpeed(Math.round(speed));
        } else if (upDifference > 1000) {
            carrier.setUpType(SpeedCarrier.K_BITS);
            speed = upDifference / 1000.0;
            carrier.setUpSpeed(Math.round(speed));
        } else {
            carrier.setUpType(SpeedCarrier.BITS);
            carrier.setUpSpeed(speed);
        }
        // Calculate dl speed
        if (dlDifference > 1000000) {
            carrier.setDlType(SpeedCarrier.M_BITS);
            speed = dlDifference / 1000000.0;
            carrier.setDlSpeed(Math.round(speed));
        } else if (dlDifference > 1000) {
            carrier.setDlType(SpeedCarrier.K_BITS);
            speed = dlDifference / 1000.0;
            carrier.setDlSpeed(Math.round(speed));
        } else {
            carrier.setDlType(SpeedCarrier.BITS);
            carrier.setDlSpeed(speed);
        }
        return carrier;
    }
}