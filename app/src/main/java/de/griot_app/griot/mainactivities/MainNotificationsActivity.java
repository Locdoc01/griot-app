package de.griot_app.griot.mainactivities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import de.griot_app.griot.baseactivities.GriotBaseActivity;
import de.griot_app.griot.R;
import de.griot_app.griot.startactivities.LoginActivity;
import de.griot_app.griot.views.TagView;

public class MainNotificationsActivity extends GriotBaseActivity {

    private static final String TAG = MainNotificationsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitle.setText(R.string.title_notifications);
        mButtonNotifications.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorGriotBlue, null));

        //TODO: löschen
        findViewById(R.id.signout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(MainNotificationsActivity.this, LoginActivity.class));
                finish();
            }
        });

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_input, null);

        final TextView textViewInputDialog = (TextView) dialogView.findViewById(R.id.textView_inputDialog);
        final EditText editTextInputDialog = (EditText) dialogView.findViewById(R.id.editText_inputDialog);

        textViewInputDialog.setText(getString(R.string.dialog_add_tag));
        editTextInputDialog.setHint(getString(R.string.hint_add_tag));

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomDialogTheme));
        // set dialog view
        alertDialogBuilder.setView(dialogView);
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(MainNotificationsActivity.this.getString(R.string.button_next),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                final TagView tagView = new TagView(MainNotificationsActivity.this);
                                final String tag = editTextInputDialog.getText().toString().trim();
                                tagView.setTag(tag);
                                tagView.getButtonDeleteTag().setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                    }
                                });

                            }
                        })
                .setNegativeButton(MainNotificationsActivity.this.getString(R.string.button_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create and show alert dialog
        alertDialogBuilder.create().show();

    }

    @Override
    protected int getSubClassLayoutId() {
        return R.layout.activity_main_notifications;
    }

    @Override
    protected String getSubClassTAG() { return TAG; }

    @Override
    protected void doOnStartAfterLoadingUserInformation() {}
}
