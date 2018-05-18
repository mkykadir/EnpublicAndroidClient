package com.mkytr.enpublic.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mkytr.enpublic.R;

public class AchievementDetailsDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_achievement_details, null);

        ImageView iAchievementDialog = v.findViewById(R.id.iAchievementDialog);
        TextView tvAchievementNameDialog = v.findViewById(R.id.tvAchievementNameDialog);

        byte[] bitmapArray = getArguments().getByteArray("image");
        Bitmap image;
        try{
            image = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        }catch(NullPointerException e){
            image = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.no_image);
        }
        String detail = getArguments().getString("detail");

        iAchievementDialog.setImageBitmap(image);
        tvAchievementNameDialog.setText(detail);

        builder.setView(v)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AchievementDetailsDialog.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }
}
