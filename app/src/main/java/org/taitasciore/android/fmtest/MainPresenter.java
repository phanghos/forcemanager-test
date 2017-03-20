package org.taitasciore.android.fmtest;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import org.taitasciore.android.storage.StationsContract;

import java.util.List;

import retrofit2.Call;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by roberto on 17/03/17.
 */

public class MainPresenter implements Presenter, ApiService.OnRequestFinishListener {

    private MainView view;
    private ApiService service;
    private Call<ApiResponse> call;

    public MainPresenter(MainView view) {
        this.view = view;
        service = new ApiService();
    }

    public void getSubwayData() {
        if (!Utils.isConnected((Activity) view)) {
            view.showNetworkError();
            return;
        }

        view.showNetworkProgress();
        if (call != null) return;
        call = service.getSubwayData(this);

        /*
        service.getApi().getSubwayData()
                //.compose(RxLoader.<ApiResponse>from((AppCompatActivity) view))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                    /*
                    .subscribe(new Observer<ApiResponse>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(ApiResponse apiResponse) {
                            Log.i("debug", "network complete");
                            mIsRequestInProcess = false;
                            if (view != null) view.hideProgress();
                            //onSuccess(apiResponse.getData().getMetro());
                        }
                    });
                    */
        /*
                .map(new Func1<ApiResponse, List<ApiResponse.Metro>>() {
                    @Override
                    public List<ApiResponse.Metro> call(ApiResponse apiResponse) {
                        return apiResponse.getData().getMetro();
                    }
                })
                .subscribe(new Observer<List<ApiResponse.Metro>>() {
                    @Override
                    public void onCompleted() {
                        view.hideProgress();
                        view.startLoader();
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.hideProgress();
                    }

                    @Override
                    public void onNext(List<ApiResponse.Metro> metros) {
                        ContentResolver cr = ((Activity) view).getContentResolver();
                        for (ApiResponse.Metro m : metros)
                            cr.insert(StationsContract.CONTENT_URI, Utils.fillValues(m));
                        onCompleted();
                    }
                });
        */
    }

    /**
     * Queries the {@link org.taitasciore.android.storage.StationsContentProvider}
     * for stations whose name includes the query and sends results back to the view
     * @param query Query to search
     */
    public void searchSuggestions(String query) {
        String selection = StationsContract.StationEntry.NAME + " LIKE '%" + query + "%'";
        Cursor c = ((Activity) view).getContentResolver().query(
                StationsContract.CONTENT_URI, null, selection, null, null, null);
        view.showSuggestions(c);
    }

    /**
     * Queries the {@link android.content.ContentProvider} for station with given ID
     */
    public void searchStationById(int id) {
        String selection = StationsContract.StationEntry._ID + " = ?";
        String[] selectionArgs = {id+""};
        Cursor c = ((Activity) view).getContentResolver().query(
                StationsContract.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null);

        ApiResponse.Metro station = null;

        if (c != null && c.moveToFirst()) {
            station = Utils.buildStation(c);
            c.close();
        }

        view.showStationInfo(station);
    }

    /**
     * Inserts stations in {@link android.content.ContentProvider} asynchronously
     * outside the UI thread by means of RxJava/RxAndroid.
     * {@link android.os.AsyncTask} or a similar class could have been used for this as well,
     * but functional reactive programming provides many advantages that these classes lack
     * @param stations List of {@link org.taitasciore.android.fmtest.ApiResponse.Metro} objects (stations)
     */
    @Override
    public void onSuccess(final List<ApiResponse.Metro> stations) {
        Observable.create(new Observable.OnSubscribe<Uri>() {
            @Override
            public void call(Subscriber<? super Uri> subscriber) {
                ContentResolver cr = ((Activity) view).getContentResolver();
                for (ApiResponse.Metro m : stations)
                    cr.insert(StationsContract.CONTENT_URI, Utils.fillValues(m));
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Uri>() {
                    @Override
                    public void onCompleted() {
                        view.hideProgress();
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.hideProgress();
                    }

                    @Override
                    public void onNext(Uri uri) {

                    }
                });

        call = null;
    }

    @Override
    public void onError() {
        view.hideProgress();
        call = null;
    }

    @Override
    public void onViewAttached(MainView view) {
        this.view = view;
    }

    @Override
    public void onViewDetached() {
        view = null;
    }
}
