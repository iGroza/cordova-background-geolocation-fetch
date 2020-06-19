package loc

class HTTPLocationManager {
}

//public static class HttpRequest extends AsyncTask<JSONObject, Void, Void> {
//
//    @Override
//    protected Void doInBackground(JSONObject... data) {
//        Log.d(TAG, "request data: " + data[0].toString());
//        HttpUrl.Builder urlBuilder = null;
//        try {
//            urlBuilder = HttpUrl.parse(data[1].getString("url")).newBuilder();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        String url = urlBuilder.build().toString();
//        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//        RequestBody body = RequestBody.create(JSON, data[0].toString());
//
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url(url)
//                .post(body)
//                .build();
//
//        Call call = client.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Request request, IOException e) {
//                Log.e("HttpService", "onFailure() Request was: " + request);
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Response r) throws IOException {
//                Log.e("response ", "onResponse(): " + r.body().string());
//            }
//        });
//        return null;
//    }
//}