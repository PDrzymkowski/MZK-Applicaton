package com.example.piotr.mzkapi15;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class SoapCommunication {



    private static final String SERVICE_URL = "https://www.w3schools.com/xml/tempconvert.asmx";
    private static final String SERVICE_NAMESPACE = "https://www.w3schools.com/xml/";
    private static final String METHOD_CEL_TO_FAHR = "CelsiusToFahrenheit";
    private static final String METHOD_FAHR_TO_CEL = "FahrenheitToCelsius";
    private static final String SOAP_ACTION_CEL_TO_FAHR = "https://www.w3schools.com/xml/CelsiusToFahrenheit";
    private static final String SOAP_ACTION_FAHR_TO_CEL = "https://www.w3schools.com/xml/FahrenheitToCelsius";


/*
    private void initButtonsOnClick() {
        OnClickListener listener = new OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btnCelsiusToFahrenheit:
                        convertCelsiusToFahrenheit();
                        break;
                    case R.id.btnFahrenheitToCelsius:
                        convertFahrenheitToCelsius();
                        break;
                    default:
                        break;
                }
            }
        };
        btnCelsiusToFahrenheit.setOnClickListener(listener);
        btnFahrenheitToCelsius.setOnClickListener(listener);
    } */



    private String callWebService(String soapAction, String method, String attribute, String val) {
        SoapObject request = prepareRequest(method, attribute, val);
        SoapSerializationEnvelope envelope = prepareSoapEnvelope(request);
        return sendRequest(soapAction, envelope);
    }

    private SoapObject prepareRequest(String method, String attribute, String val) {
        SoapObject request = new SoapObject(SERVICE_NAMESPACE, method);
        request.addProperty(attribute, val);
        return request;
    }

    private SoapSerializationEnvelope prepareSoapEnvelope(SoapObject request) {
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;
        return envelope;
    }

    private String sendRequest(String soapAction, SoapSerializationEnvelope envelope) {
        HttpTransportSE httpTransportSE = new HttpTransportSE(SERVICE_URL);
        try {
            httpTransportSE.call(soapAction, envelope);
            //  SoapObject result = (SoapObject) envelope.bodyIn;
            SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
            return result.toString();
        } catch (Exception e) {

            e.printStackTrace();
            return "";

        }
    }
}


