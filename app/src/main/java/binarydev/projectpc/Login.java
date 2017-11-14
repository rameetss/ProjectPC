package binarydev.projectpc;

import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import binarydev.projectpc.R;

public class Login extends AppCompatActivity
{
    final Random rnd = new Random();

    @Override
    protected void onCreate(
            final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_layout);

        final ImageView img = (ImageView) findViewById(R.id.imgRandom);
        final String str = "pc" + rnd.nextInt(6);
        img.setImageDrawable
                (
                        getResources().getDrawable(getResourceID(str, "drawable",
                                getApplicationContext()))
                );
    }

    protected final static int getResourceID
            (final String resName, final String resType, final Context ctx)
    {
        final int ResourceID =
                ctx.getResources().getIdentifier(resName, resType,
                        ctx.getApplicationInfo().packageName);
        if (ResourceID == 0)
        {
            throw new IllegalArgumentException
                    (
                            "No resource string found with name " + resName
                    );
        }
        else
        {
            return ResourceID;
        }
    }
}