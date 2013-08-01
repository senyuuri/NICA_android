package com.dhs.nica;

/**
 * Created by natsuyuu on 13-7-28.
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */

import android.R;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 *
 */
public abstract class BaseActivity extends Activity {

    protected ImageLoader imageLoader = ImageLoader.getInstance();


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu, menu);
        menu.add(0,1,1,com.dhs.nica.R.string.menu1);
        menu.add(0,2,2,com.dhs.nica.R.string.menu2);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case 1:
                Toast toast = Toast.makeText(this,"Developing feature",Toast.LENGTH_LONG);
                toast.show();

            case 2:
                Toast toast2 = Toast.makeText(this,"Developing feature2",Toast.LENGTH_LONG);
                toast2.show();
            default:
                return false;
        }
    }

}
