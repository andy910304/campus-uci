package cu.uci.market;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import cu.uci.campusuci.R;

/**
 * Created by Yannier on 6/12/2015.
 */
public class FragmentApps extends Fragment{

    View apps;
    GridView gridView;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        apps = inflater.inflate(R.layout.fragment_apps,container,false);
        gridView = (GridView) apps.findViewById(R.id.grid_apps);

        return apps;
    }
}
