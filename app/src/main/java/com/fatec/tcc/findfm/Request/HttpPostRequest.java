package com.fatec.tcc.findfm.Request;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.fatec.tcc.findfm.Infrastructure.Volley.ErrorResponseException;
import com.fatec.tcc.findfm.Infrastructure.Volley.JsonPOSTRequest;
import com.fatec.tcc.findfm.Infrastructure.Volley.SharedRequestQueue;

//import java.util.function.Consumer;

public class HttpPostRequest<TRequest, TResponse, TErrorResponse> {/*
    private final Class<TRequest> requestClass;
    private final Class<TResponse> receiveClass;
    private final Class<TErrorResponse> errorResponseClass;

    private final Consumer<TResponse> onSuccess;
    private final Consumer<TErrorResponse> onBusinessError;
    private final Consumer<Exception> onCriticalError;

    private String fullUrl;
    private TRequest requestObject;

    private JsonPOSTRequest<TRequest, TResponse, TErrorResponse> request;

    public HttpPostRequest(Class<TRequest> requestClass,
                           Class<TResponse> receiveClass,
                           Class<TErrorResponse> errorResponseClass,
                           Consumer<TResponse> onSuccess,
                           Consumer<TErrorResponse> onBusinessError,
                           Consumer<Exception> onCriticalError)
    {
        this.requestClass = requestClass;
        this.receiveClass = receiveClass;
        this.errorResponseClass = errorResponseClass;
        this.onSuccess = onSuccess;
        this.onBusinessError = onBusinessError;
        this.onCriticalError = onCriticalError;

        initRequest();
    }

    private void initRequest() {
        request = new JsonPOSTRequest<>
        (
            requestClass,
            receiveClass,
            errorResponseClass,
            fullUrl,
            requestObject,
            (TResponse response) ->
            {
                // Logging...?
                onSuccess.accept(response);
            },
            (VolleyError error) ->
            {
                Throwable innerException = error.getCause();
                if(innerException instanceof ErrorResponseException)
                {
                    ErrorResponseException errorResponseException = (ErrorResponseException) innerException;
                    TErrorResponse errorResponse = (TErrorResponse) errorResponseException.getErrorResponse();
                    // Logging...?
                    onBusinessError.accept(errorResponse);
                }
                else
                {
                    // Logging...?
                    onCriticalError.accept(error);
                }
            }
        );
    }

    public void execute(Context context) {
        RequestQueue queue = SharedRequestQueue.getRequestQueue(context);
        queue.add(request);
    }

    public String getFullUrl() {
        return fullUrl;
    }

    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
    }

    public TRequest getRequestObject() {
        return requestObject;
    }

    public void setRequestObject(TRequest requestObject) {
        this.requestObject = requestObject;
    }*/
}
