package com.dhs.nica;

/**
 * Created by natsuyuu on 13-7-28.
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */

import android.R;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 *
 */
public abstract class BaseActivity extends Activity {

    protected ImageLoader imageLoader = ImageLoader.getInstance();

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*
            case R.id.item_clear_memory_cache:
                imageLoader.clearMemoryCache();
                return true;
            case R.id.item_clear_disc_cache:
                imageLoader.clearDiscCache();
                return true;

            default:
                return false;
        }
    }
    */
}
