package com.evernews.evernews;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewsAddListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewsAddListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsAddListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TYPE_KEY = "type_key";
    private static final String TAB_NAME = "tab_name";

    // TODO: Rename and change types of parameters
    private int mParam1;
    private String mParam2;
    private String predefCats[]={"Featured","Popular","Categories"};
    public NewsAddListFragment() {
        // Required empty public constructor
    }

    public static NewsAddListFragment newInstance(int sectionNumber, String tabName) {
        Bundle args = new Bundle();
        args.putSerializable(TYPE_KEY, sectionNumber);
        args.putSerializable(TAB_NAME, tabName);
        NewsAddListFragment fragment = new NewsAddListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewsAddListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewsAddListFragment newInstanceREE(int param1, String param2) {
        NewsAddListFragment fragment = new NewsAddListFragment();
        Bundle args = new Bundle();
        args.putInt(TYPE_KEY, param1);
        args.putString(TAB_NAME, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(TYPE_KEY);
            mParam2 = getArguments().getString(TAB_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_tab, container, false);
        final ListView listView = (ListView)rootView.findViewById(R.id.list);
        final List<ListItemObject> allItems = loadDataTab1(getArguments().getInt(TYPE_KEY));
        CustomListAdapter customAdapter = new CustomListAdapter(getActivity(), allItems);
        listView.setAdapter(customAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                String RSSUID=allItems.get(position).getChannelRSSID();
                new AddTabNewsPreview().setRSSUID(RSSUID).setChannelDetails(allItems.get(position).getChannelMeta()).setListener(new AddTabNewsPreview.AddListener() {
                    @Override
                    public void onAdd(AddTabNewsPreview dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onRemove(AddTabNewsPreview dialog) {
                        dialog.dismiss();
                    }
                }).show(getFragmentManager(), "#add");
            }
        });
        return rootView;
    }

    private List<ListItemObject> loadDataTab1(int ii) {
        List<ListItemObject> items = new ArrayList<>();
        try {
            if (ii == 0) {
                for (int i = 0; i < Main.catListArray.length; i++) {
                    if (Main.catListArray[i][6].compareToIgnoreCase(predefCats[0]) == 0) {
                        String channelLogo = Main.catListArray[i][0];
                        String channelTitle = Main.catListArray[i][5];
                        String channelMeta = Main.catListArray[i][3];
                        String channelRSSID = Main.catListArray[i][0];
                        String categoryType = Main.catListArray[i][6];
                        String channelRSSURL = Main.catListArray[i][1];
                        items.add(new ListItemObject(channelLogo, channelTitle, channelMeta, channelRSSID, categoryType,channelRSSURL));
                    /*
                     Main.catListArray[i][0] = (parser.getValue(e, "RSSUrlId"));
            Main.catListArray[i][1] = (parser.getValue(e, "RSSURL"));
            Main.catListArray[i][2] = (parser.getValue(e, "RSSTitle"));
            Main.catListArray[i][3] = (parser.getValue(e, "Detail"));
            Main.catListArray[i][4] = (parser.getValue(e, "Comment"));
            Main.catListArray[i][5] = (parser.getValue(e, "MediaHouse"));
            Main.catListArray[i][6] = (parser.getValue(e, "NewsType"));
                     */
                    }
                }
            } else if (ii == 1) {
                for (int i = 0; i < Main.catListArray.length; i++) {
                    if (Main.catListArray[i][6].compareToIgnoreCase(predefCats[1]) == 0) {
                        String channelLogo = Main.catListArray[i][0];
                        String channelTitle = Main.catListArray[i][5];
                        String channelMeta = Main.catListArray[i][3];
                        String channelRSSID = Main.catListArray[i][0];
                        String categoryType = Main.catListArray[i][6];
                        String channelRSSURL = Main.catListArray[i][1];
                        items.add(new ListItemObject(channelLogo, channelTitle, channelMeta, channelRSSID, categoryType,channelRSSURL));
                    }
                }
            } else {
                for (int i = 0; i < Main.catListArray.length; i++) {
                    if (Main.catListArray[i][6].compareToIgnoreCase(predefCats[1]) == 0) {continue;}
                    else
                    if (Main.catListArray[i][6].compareToIgnoreCase(predefCats[0]) == 0) {continue;}
                    else{
                        String channelLogo = Main.catListArray[i][0];
                        String channelTitle = Main.catListArray[i][5];
                        String channelMeta = Main.catListArray[i][3];
                        String channelRSSID = Main.catListArray[i][0];
                        String categoryType = Main.catListArray[i][6];
                        String channelRSSURL = Main.catListArray[i][1];
                        items.add(new ListItemObject(channelLogo, channelTitle, channelMeta, channelRSSID, categoryType,channelRSSURL));
                    }
                }
            }
        }catch(Exception e){}
        return items;
    }
}