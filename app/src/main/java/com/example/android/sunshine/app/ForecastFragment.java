/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine.app;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.sunshine.app.data.WeatherContract;
import com.example.android.sunshine.app.sync.SunshineSyncAdapter;

/**
 * Encapsulates fetching the forecast and displaying it as a {@link ListView} layout.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = ForecastFragment.class.getSimpleName();
    //    private ArrayAdapter<String> mForecastAdapter;
    private ForecastAdapter mForecastAdapter;

    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";

    private boolean mUseTodayLayout;

    private static final int FORECAST_LOADER = 0;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    // For the forecast view we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_LOCATION_SETTING = 5;
    static final int COL_WEATHER_CONDITION_ID = 6;
    static final int COL_COORD_LAT = 7;
    static final int COL_COORD_LONG = 8;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);
    }

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
//        Log.v(LOG_TAG, "fragment in onCreate");
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
//        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.forecastfragment, menu);
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_refresh) {
//            updateWeather();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Create some dummy data for the ListView.  Here's a sample weekly forecast
//        String[] data = {
//                "Mon 6/23 - Sunny - 31/17",
//                "Tue 6/24 - Foggy - 21/8",
//                "Wed 6/25 - Cloudy - 22/17",
//                "Thurs 6/26 - Rainy - 19/11",
//                "Fri 6/27 - Foggy - 21/10",
//                "Sat 6/28 - TRAPPED IN WEATHERSTATION - 23/18",
//                "Sun 6/29 - Sunny - 20/7"
//        };
//        List<String> weekForecast = new ArrayList<String>(Arrays.asList(data));

//        // Now that we have some dummy forecast data, create an ArrayAdapter.
//        // The ArrayAdapter will take data from a source (like our dummy forecast) and
//        // use it to populate the ListView it's attached to.
//        mForecastAdapter =
//                new ArrayAdapter<String>(
//                        getActivity(), // The current context (this activity)
//                        R.layout.list_item_forecast, // The name of the layout ID.
//                        R.id.list_item_forecast_textview, // The ID of the textview to populate.
//                        new ArrayList<String>());
//                        weekForecast);

//        String locationSetting = Utility.getPreferredLocation(getActivity());
//
//        // Sort order:  Ascending, by date.
//        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
//        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
//                locationSetting, System.currentTimeMillis());
//
//        Cursor cur = getActivity().getContentResolver().query(weatherForLocationUri,
//                null, null, null, sortOrder);
//
//        // The CursorAdapter will take data from our cursor and populate the ListView
//        // However, we cannot use FLAG_AUTO_REQUERY since it is deprecated, so we will end
//        // up with an empty list the first time we run.
//        mForecastAdapter = new ForecastAdapter(getActivity(), cur, 0);

        // The CursorAdapter will take data from our cursor and populate the ListView.
        mForecastAdapter = new ForecastAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.container);
        mSwipeRefreshLayout.setColorScheme(R.color.blue,
                R.color.green, R.color.orange, R.color.purple);

        mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.e(LOG_TAG, "refresh");
                updateWeather();
                Toast.makeText(getActivity(), "Update Weather", Toast.LENGTH_SHORT).show();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        updateSelectScrollToPosition(50);
                    }
                }, 3000);
            }
        });

        // Get a reference to the ListView, and attach this adapter to it.
//        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
//        listView.setAdapter(mForecastAdapter);
        mListView = (ListView) rootView.findViewById(R.id.listview_forecast);
        mListView.setAdapter(mForecastAdapter);

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                String forecast = mForecastAdapter.getItem(position);
////                Toast.makeText(getActivity(), forecast, Toast.LENGTH_SHORT).show();
//                Intent forecastIntent = new Intent(getActivity(), DetailActivity.class)
//                        .putExtra(Intent.EXTRA_TEXT, forecast);
////                Log.v(LOG_TAG,Intent.EXTRA_TEXT);
//                startActivity(forecastIntent);
//            }
//        });


        // We'll call our MainActivity
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    String locationSetting = Utility.getPreferredLocation(getActivity());
//                    Intent intent = new Intent(getActivity(), DetailActivity.class)
//                            .setData(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
//                                    locationSetting, cursor.getLong(COL_WEATHER_DATE)
//                            ));

                    ((Callback) getActivity())
                            .onItemSelected(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                                    locationSetting, cursor.getLong(COL_WEATHER_DATE)
                            ));

                    if (!mUseTodayLayout)
                        mPosition = position;

//                    Log.v(LOG_TAG + " test",WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
//                            locationSetting, cursor.getLong(COL_WEATHER_DATE)).toString());
                    // content://com.example.android.sunshine.app/weather/94043/1496872800000

//                    startActivity(intent);
                }
            }
        });

        // If there's instance state, mine it for useful information.
        // The end-goal here is that the user never knows that turning their device sideways
        // does crazy lifecycle related things.  It should feel like some stuff stretched out,
        // or magically appeared to take advantage of room, but data or place in the app was never
        // actually *lost*.
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        mForecastAdapter.setUseTodayLayout(mUseTodayLayout);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * Connect Activity with our loader (FORECAST_LOADER)
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
//        Log.v(LOG_TAG, "fragment in onActivityCreated");
        //FORECAST_LOADER: our loader ID
        if (getLoaderManager().getLoader(FORECAST_LOADER) == null) {
            getLoaderManager().initLoader(FORECAST_LOADER, null, this);

//            Loader<Cursor> mCursorLoader = getLoaderManager().getLoader(FORECAST_LOADER);

//            mCursorLoader.registerListener(FORECAST_LOADER, new Loader.OnLoadCompleteListener<Cursor>() {
//                        @Override
//                        public void onLoadComplete(Loader<Cursor> loader, Cursor data) {
//                            loader.unregisterListener(this);
//                            selectScrollToPosition();
//                        }
//                    }
//            );

        } else {
            getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
//        Log.v(LOG_TAG, "fragment in onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
//        Log.v(LOG_TAG, "fragment in onResume");
        super.onResume();
//        selectScrollToPosition();

        updateSelectScrollToPosition(50);
    }

    @Override
    public void onPause() {
//        Log.v(LOG_TAG, "fragment in onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
//        Log.v(LOG_TAG, "fragment in onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
//        Log.v(LOG_TAG, "fragment in onDestroy");
        super.onDestroy();
    }

    // since we read the location when we create the loader, all we need to do is restart things
    void onLocationChanged() {
        updateWeather();
        // restart loader because the loader has URI for old location settings
        getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
        updateSelectScrollToPosition(1000);
    }

    private void updateWeather() {
        /*
         * One of the files that has been in your app since we’ve added
         * the WeatherProvider is a new, database-friendly FetchWeatherTask
         */
//        FetchWeatherTask weatherTask = new FetchWeatherTask();
//        FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity(), mForecastAdapter);

//            weatherTask.execute("94043");   //mansoura,egypt    //94043

        // PreferenceManager is normally used when you want to create
        // a PreferenceActivity or load in some Preferences from an .xml
        // file in your application with default values, and holds it's
        // own referenced to

        // getDefaultSharedPreferences(Context context)
        // Gets a SharedPreferences instance that points to the default
        // file that is used by the preference framework in the given context
//        SharedPreferences prefs = PreferenceManager
//                .getDefaultSharedPreferences(getActivity());
//        SharedPreferences preffs = this.getActivity()
//                .getSharedPreferences("com.example.android.sunshine.app_preferences"
//                        , Context.MODE_PRIVATE);

//        Log.v(LOG_TAG, getActivity().getPackageName());
        // prints: V/ForecastFragment: com.example.android.sunshine.app


        // defValue: Value to return if this preference does not exist
//        String location = prefs.getString(getString(R.string.pref_location_key)
//                , getString(R.string.pref_location_default));
//            Log.v(LOG_TAG, location);


//        FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity());
//        String location = Utility.getPreferredLocation(getActivity());
//        weatherTask.execute(location);


//        Intent intent = new Intent(getActivity(), SunshineService.class);
//        intent.putExtra(SunshineService.LOCATION_QUERY_EXTRA,
//                Utility.getPreferredLocation(getActivity()));
//        getActivity().startService(intent);


//        Intent alarmIntent = new Intent(getActivity(), SunshineService.AlarmReceiver.class);
//        alarmIntent.putExtra(SunshineService.LOCATION_QUERY_EXTRA, Utility.getPreferredLocation(getActivity()));
//
//        //Wrap in a pending intent which only fires once.
//        //getBroadcast(context, 0, i, 0);
//        PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0,alarmIntent,PendingIntent.FLAG_ONE_SHOT);
//
//        AlarmManager am=(AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
//
//        //Set the AlarmManager to wake up the system.
//        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, pi);

//        Log.v(LOG_TAG, "update weather");
        SunshineSyncAdapter.syncImmediately(getActivity());
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        updateWeather();
//    }

    /**
     * Define the loader by specific URI (Table in DB) and specific Columns
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String locationSetting = Utility.getPreferredLocation(getActivity());

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
//        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " DESC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting, System.currentTimeMillis());

//        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
//                locationSetting, System.currentTimeMillis() - 2*24*60*60*1000);

//        Log.v(LOG_TAG + " tests", weatherForLocationUri.toString());
        // weatherForLocationUri
        // content://com.example.android.sunshine.app/weather/94043?date=1496700000000

//        Log.v(LOG_TAG + " tests", FORECAST_COLUMNS[2]);
        // FORECAST_COLUMNS[2]: short_desc

        return new CursorLoader(getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder);
    }

    /**
     * Connect listView Adapter with our loader
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        Log.v(LOG_TAG, "In onLoadFinished");
        mForecastAdapter.swapCursor(data);
//        selectScrollToPosition();
    }

    /**
     * The onLoaderReset(Loader) method only gets called when our loader
     * is being destroyed.
     * So, it is asked to release all references to the Cursor.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mForecastAdapter.swapCursor(null);
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
        if (mForecastAdapter != null) {
            mForecastAdapter.setUseTodayLayout(mUseTodayLayout);
        }
    }

    private void update(Runnable runnable, int delayTime) {
        View rootView = getView();
        if (isAdded() && rootView != null) {
            rootView.postDelayed(runnable, delayTime);
        }
    }

    private void updateSelectScrollToPosition(int delayTime) {
        update(new Runnable() {
            @Override
            public void run() {
                selectScrollToPosition();
            }
        }, delayTime);
    }

    private void selectScrollToPosition() {
        if (!mUseTodayLayout) {
            Cursor cursor = null;
            if (mPosition == ListView.INVALID_POSITION) {
//                Log.v(LOG_TAG, "fragment in onResume / ListView.INVALID_POSITION");
                mListView.setItemChecked(0, true);
                cursor = (Cursor) mListView.getAdapter().getItem(0);
//                Log.v(LOG_TAG, cursor.toString());

                if (cursor != null) {
                    mPosition = 0;
//                Log.v(LOG_TAG, String.valueOf(cursor.getLong(COL_WEATHER_DATE)));
                    String locationSetting = Utility.getPreferredLocation(getActivity());
                    ((Callback) getActivity())
                            .onItemSelected(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                                    locationSetting, cursor.getLong(COL_WEATHER_DATE)
                            ));
                }
            } else {
                mListView.setItemChecked(mPosition, true);
                mListView.smoothScrollToPosition(mPosition);
//                cursor = (Cursor) mListView.getAdapter().getItem(mPosition);
            }
        } else {
            mListView.smoothScrollToPosition(0);
        }
    }

/*
 * delete the FetchWeatherTask inner class from within the ForecastFragment
 */
//    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {
//
//        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
//
//        /* The date/time conversion code is going to be moved outside the asynctask later,
//         * so for convenience we're breaking it out into its own method now.
//         */
//        private String getReadableDateString(long time) {
//            // Because the API returns a unix timestamp (measured in seconds),
//            // it must be converted to milliseconds in order to be converted to valid date.
//            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
//            return shortenedDateFormat.format(time);
//        }
//
//        /**
//         * Prepare the weather high/lows for presentation.
//         */
//        private String formatHighLows(double high, double low, String temp_unit) {
//
//
////            Log.v(LOG_TAG, getResources().getStringArray(R.array.pref_temp_unit_values)[1]);
//
//            if (temp_unit.equals(getString(R.string.pref_temp_unit_imperial))) {
//                high = (high * 1.8) + 32;
//                low = (low * 1.8) + 32;
//            }
//
//            // For presentation, assume the user doesn't care about tenths of a degree.
//            long roundedHigh = Math.round(high);
//            long roundedLow = Math.round(low);
//
//            String highLowStr = roundedHigh + "/" + roundedLow;
//            return highLowStr;
//        }
//
//        /**
//         * Take the String representing the complete forecast in JSON Format and
//         * pull out the data we need to construct the Strings needed for the wireframes.
//         * <p/>
//         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
//         * into an Object hierarchy for us.
//         */
//        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
//                throws JSONException {
//
//            // These are the names of the JSON objects that need to be extracted.
//            final String OWM_CITY = "city";
//            final String OWM_CITY_NAME = "name";
//            final String OWM_LIST = "list";
//            final String OWM_WEATHER = "weather";
//            final String OWM_TEMPERATURE = "temp";
//            final String OWM_MAX = "max";
//            final String OWM_MIN = "min";
//            final String OWM_DESCRIPTION = "main";
//
//            JSONObject forecastJson = new JSONObject(forecastJsonStr);
//            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);
//
//            String city_name = forecastJson.getJSONObject(OWM_CITY)
//                    .getString(OWM_CITY_NAME);
////            Log.v(LOG_TAG, "city_name: " + city_name);
//
//            // OWM returns daily forecasts based upon the local time of the city that is being
//            // asked for, which means that we need to know the GMT offset to translate this data
//            // properly.
//
//            // Since this data is also sent in-order and the first day is always the
//            // current day, we're going to take advantage of that to get a nice
//            // normalized UTC date for all of our weather.
//
//            Time dayTime = new Time();
//            dayTime.setToNow();
//
//            // we start at the day returned by local time. Otherwise this is a mess.
//            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);
//
//            // now we work exclusively in UTC
//            dayTime = new Time();
//
//            SharedPreferences prefs = PreferenceManager
//                    .getDefaultSharedPreferences(getActivity());
//            String temp_unit = prefs.getString(getString(R.string.pref_temp_unit_key)
//                    , getString(R.string.pref_temp_unit_default));
//
//            // missing up with the counter i
//            // missing up with the counter i
//            // missing up with the counter i
//            String[] resultStrs = new String[numDays + 1];
//            resultStrs[0] = city_name;
//            for (int i = 1; i < weatherArray.length() + 1; i++) {
//                // For now, using the format "Day, description, hi/low"
//                String day;
//                String description;
//                String highAndLow;
//
//                // Get the JSON object representing the day
//                JSONObject dayForecast = weatherArray.getJSONObject(i - 1);
//
//                // The date/time is returned as a long.  We need to convert that
//                // into something human-readable, since most people won't read "1400356800" as
//                // "this saturday".
//                long dateTime;
//                // Cheating to convert this to UTC time, which is what we want anyhow
//                dateTime = dayTime.setJulianDay(julianStartDay + i - 1);
//                day = getReadableDateString(dateTime);
//
//                // description is in a child array called "weather", which is 1 element long.
//                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
//                description = weatherObject.getString(OWM_DESCRIPTION);
//
//                // Temperatures are in a child object called "temp".  Try not to name variables
//                // "temp" when working with temperature.  It confuses everybody.
//                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
//                double high = temperatureObject.getDouble(OWM_MAX);
//                double low = temperatureObject.getDouble(OWM_MIN);
//
//                highAndLow = formatHighLows(high, low, temp_unit);
//                resultStrs[i] = day + " - " + description + " - " + highAndLow;
//            }
//
////            for (String s : resultStrs) {
////                Log.v(LOG_TAG, "Forecast entry: " + s);
////            }
//            return resultStrs;
//        }
//
//        @Override
//        protected String[] doInBackground(String... params) {
//            // If there's no zip code, there's nothing to look up.  Verify size of params.
//            if (params.length == 0) {
//                return null;
//            }
//
//            // These two need to be declared outside the try/catch
//            // so that they can be closed in the finally block.
//            HttpURLConnection urlConnection = null;
//            BufferedReader reader = null;
//
//            // Will contain the raw JSON response as a string.
//            String forecastJsonStr = null;
//
//            String format = "json";
//            String units = "metric";
//            int numDays = 7;
//
//            try {
//                // Construct the URL for the OpenWeatherMap query
//                // Possible parameters are avaiable at OWM's forecast API page, at
//                // http://openweathermap.org/API#forecast
//                //http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7&APPID=4e72884d73ef8321a2c976567a88ce7e
//
//                final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily";
//                final String QUERY_PARAM = "q";
//                final String FORMAT_PARAM = "mode";
//                final String UNITS_PARAM = "units";
//                final String DAYS_PARAM = "cnt";
//                final String APPID_PARAM = "APPID";
//
//                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
//                        .appendQueryParameter(QUERY_PARAM, params[0])
//                        .appendQueryParameter(FORMAT_PARAM, format)
//                        .appendQueryParameter(UNITS_PARAM, units)
//                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
//                        .appendQueryParameter(APPID_PARAM, BuildConfig.OPEN_WEATHER_MAP_API_KEY)
//                        .build();
//
//                String myUrl = builtUri.toString();
//                URL url = new URL(myUrl);
//
////                Log.v(LOG_TAG, "myUrl: " + myUrl);
//
//                // Create the request to OpenWeatherMap, and open the connection
//                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("GET");
//                urlConnection.connect();
//
//                // Read the input stream into a String
//                InputStream inputStream = urlConnection.getInputStream();
//                StringBuffer buffer = new StringBuffer();
//                if (inputStream == null) {
//                    // Nothing to do.
//                    return null;
//                }
//                reader = new BufferedReader(new InputStreamReader(inputStream));
//
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
//                    // But it does make debugging a *lot* easier if you print out the completed
//                    // buffer for debugging.
//                    buffer.append(line + "\n");
//                }
//
//                if (buffer.length() == 0) {
//                    // Stream was empty.  No point in parsing.
//                    return null;
//                }
//                forecastJsonStr = buffer.toString();
//
////                Log.v(LOG_TAG, "Forecast JSON String: " + forecastJsonStr);
//
//            } catch (IOException e) {
//                Log.e(LOG_TAG, "Error getting data ", e);
//                // If the code didn't successfully get the weather data, there's no point in attemping
//                // to parse it.
//                return null;
//            } finally {
//                if (urlConnection != null) {
//                    urlConnection.disconnect();
//                }
//                if (reader != null) {
//                    try {
//                        reader.close();
//                    } catch (final IOException e) {
//                        Log.e(LOG_TAG, "Error closing stream", e);
//                    }
//                }
//            }
//
//            try {
//                return getWeatherDataFromJson(forecastJsonStr, numDays);
//            } catch (JSONException e) {
//                Log.e(LOG_TAG, e.getMessage(), e);
//                e.printStackTrace();
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String[] strings) {
//            super.onPostExecute(strings);
////            for (String s : strings) {
////                Log.v(LOG_TAG, "Forecast entry: " + s);
////            }
//
//            if (strings != null) {
//                mForecastAdapter.clear();
//                for (String dayForecastStr : strings) {
//                    mForecastAdapter.add(dayForecastStr);
//                }
//            }
//        }
//    }

}