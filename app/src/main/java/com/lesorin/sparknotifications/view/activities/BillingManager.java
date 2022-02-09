package com.lesorin.sparknotifications.view.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.lesorin.sparknotifications.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class BillingManager implements PurchasesUpdatedListener, BillingClientStateListener
{
    private final MainActivity _mainActivity;
    private final BillingClient _billingClient; //Billing client used for queries and purchases on Google Play.
    private Map<String, SkuDetails> _skuDetailsCache;
    private final List<String> _skusList;

    public BillingManager(MainActivity mainActivity)
    {
        _mainActivity = mainActivity;
        _skuDetailsCache = new HashMap<>();
        _skusList = Arrays.asList(_mainActivity.getResources().getStringArray(R.array.DonationSKUs));
        _billingClient = BillingClient.newBuilder(_mainActivity).setListener(this).enablePendingPurchases().build();

        _billingClient.startConnection(this);
    }

    @Override
    public void onBillingSetupFinished(@NonNull BillingResult billingResult)
    {
        if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK)
        {
            querySKUs();
            queryPurchases();
        }
    }

    @Override
    public void onBillingServiceDisconnected()
    {
    }

    private void querySKUs()
    {
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();

        params.setSkusList(_skusList).setType(BillingClient.SkuType.INAPP);
        _billingClient.querySkuDetailsAsync(params.build(), (billingResult, skuDetailsList) ->
        {
            if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null)
            {
                //Add each SkuDetails to the cache.
                for(SkuDetails skuDetails : skuDetailsList)
                {
                    _skuDetailsCache.put(skuDetails.getSku(), skuDetails);
                }

                //Create a list with the items, in the order they are in _skusList.
                ArrayList<String> itemsList = new ArrayList<>();

                for(String sku : _skusList)
                {
                    itemsList.add("Donate " + _skuDetailsCache.get(sku).getPrice());
                }

                _mainActivity.purchaseItemsAcquired(itemsList);
            }
        });
    }

    private void queryPurchases()
    {
        if(_billingClient != null && _billingClient.isReady())
        {
            _billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP, (billingResult, list) ->
            {
                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK)
                {
                    handlePurchasesList(list);
                }
            });
        }
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchasesList)
    {
        /*All purchases reported here must either be consumed or acknowledged.
        Failure to either consume (via consumeAsync(ConsumeParams, ConsumeResponseListener))
        or acknowledge (via acknowledgePurchase(AcknowledgePurchaseParams, AcknowledgePurchaseResponseListener))
        a purchase will result in that purchase being refunded.*/

        //Handle the purchase if the purchase succeeded, or if the item is already owned.
        if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK ||
            billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED)
        {
            //Loop through the purchase list and process everything the user has purchased.
            //(In the case of this app, the user can only purchase 1 thing: Removal of ads.)
            handlePurchasesList(purchasesList);
        }
        else
        {
            _mainActivity.runOnUiThread(_mainActivity::purchaseCancelled);
        }
    }

    private void handlePurchasesList(List<Purchase> purchasesList)
    {
        if(purchasesList != null)
        {
            for(Purchase purchase : purchasesList)
            {
                if(purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED)
                {
                    //Grant access to the purchase here, and show a dialog informing about it.
                    handlePurchase(purchase);
                }
                else
                {
                    if(purchase.getPurchaseState() == Purchase.PurchaseState.PENDING)
                    {
                        // Here you can confirm to the user that they've started the pending
                        // purchase, and to complete it, they should follow instructions that
                        // are given to them. You can also choose to remind the user in the
                        // future to complete the purchase if you detect that it is still
                        // pending.
                        //_mainActivity.runOnUiThread(_mainActivity::purchasePending);
                    }
                }
            }
        }
    }

    private void handlePurchase(Purchase purchase)
    {
        acknowledgePurchase(purchase);

        //Enable whatever item in the app here.
        //...
    }

    private void acknowledgePurchase(Purchase purchase)
    {
        if(!purchase.isAcknowledged())
        {
            AcknowledgePurchaseParams acknowledgePurchaseParams =  AcknowledgePurchaseParams.newBuilder().
                    setPurchaseToken(purchase.getPurchaseToken()).build();

            _billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult ->
            {
                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK)
                {
                    _mainActivity.runOnUiThread(_mainActivity::purchaseCompleted);

                    //Consume the purchase to allow the user to buy the item again if they want.
                    consumePurchase(purchase);
                }
            });
        }
    }

    private void consumePurchase(Purchase purchase)
    {
        ConsumeParams consumeParams = ConsumeParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build();

        _billingClient.consumeAsync(consumeParams, (billingResult, purchaseToken) -> { });
    }

    void endConnection()
    {
        //This is the recommended place to dispose of the BillingClient.
        if(_billingClient != null)
        {
            _billingClient.endConnection();
        }
    }

    void startBillingFlow(int option)
    {
        String sku = _skusList.get(option - 1);

        if(_billingClient != null)
        {
            BillingFlowParams flowParams = BillingFlowParams.newBuilder().setSkuDetails(_skuDetailsCache.get(sku)).build();

            //launchBillingFlow() will deliver the result to onPurchasesUpdated().
            _billingClient.launchBillingFlow(_mainActivity, flowParams);
        }
        else
        {
            _mainActivity.runOnUiThread(_mainActivity::cannotStartBillingFlow);
        }
    }
}