package binarydev.projectpc;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

/**
 * Created by Rameet on 11/12/2017.
 */

public class cpu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_ram) {
            Intent ram = new Intent(this, ram.class);
            startActivity(ram);
        } else if (id == R.id.nav_cpu) {
            Intent cpu = new Intent(this, cpu.class);
            startActivity(cpu);
        } else if (id == R.id.nav_gpu) {
            Intent gpu = new Intent(this, gpu.class);
            startActivity(gpu);
        } else if (id == R.id.nav_motherboard) {
            Intent motherboard = new Intent(this, motherboard.class);
            startActivity(motherboard);
        } else if (id == R.id.nav_hdd) {
            Intent hdd = new Intent(this, hdd.class);
            startActivity(hdd);
        } else if (id == R.id.nav_ssd) {
            Intent ssd = new Intent(this, ssd.class);
            startActivity(ssd);
        }else if (id == R.id.nav_case) {
            Intent Case = new Intent(this, computerCase.class);
            startActivity(Case);
        }else if (id == R.id.nav_cooling) {
            Intent cooling = new Intent(this, cooling.class);
            startActivity(cooling);
        }else if (id == R.id.nav_monitor) {
            Intent monitor = new Intent(this, monitor.class);
            startActivity(monitor);
        }else if (id == R.id.nav_powerSupply) {
            Intent powerSupply = new Intent(this, power_supply.class);
            startActivity(powerSupply);
        }else if (id == R.id.nav_peripherals) {
            Intent peripherals = new Intent(this, peripherals.class);
            startActivity(peripherals);
        }else if (id == R.id.nav_misc) {
            Intent misc = new Intent(this, misc.class);
            startActivity(misc);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
