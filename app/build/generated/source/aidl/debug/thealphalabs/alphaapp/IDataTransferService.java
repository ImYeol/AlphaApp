/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/yeol/AndroidStudioProjects/AlphaApp/app/src/main/aidl/thealphalabs/alphaapp/IDataTransferService.aidl
 */
package thealphalabs.alphaapp;
// Declare any non-default types here with import statements

public interface IDataTransferService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements thealphalabs.alphaapp.IDataTransferService
{
private static final java.lang.String DESCRIPTOR = "thealphalabs.alphaapp.IDataTransferService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an thealphalabs.alphaapp.IDataTransferService interface,
 * generating a proxy if needed.
 */
public static thealphalabs.alphaapp.IDataTransferService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof thealphalabs.alphaapp.IDataTransferService))) {
return ((thealphalabs.alphaapp.IDataTransferService)iin);
}
return new thealphalabs.alphaapp.IDataTransferService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_connectTo:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.connectTo(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_transferMouseData:
{
data.enforceInterface(DESCRIPTOR);
float _arg0;
_arg0 = data.readFloat();
float _arg1;
_arg1 = data.readFloat();
int _arg2;
_arg2 = data.readInt();
this.transferMouseData(_arg0, _arg1, _arg2);
return true;
}
case TRANSACTION_transferStringData:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.transferStringData(_arg0);
return true;
}
case TRANSACTION_transferGyroData:
{
data.enforceInterface(DESCRIPTOR);
float _arg0;
_arg0 = data.readFloat();
float _arg1;
_arg1 = data.readFloat();
float _arg2;
_arg2 = data.readFloat();
this.transferGyroData(_arg0, _arg1, _arg2);
return true;
}
case TRANSACTION_transferAccelData:
{
data.enforceInterface(DESCRIPTOR);
float _arg0;
_arg0 = data.readFloat();
float _arg1;
_arg1 = data.readFloat();
float _arg2;
_arg2 = data.readFloat();
this.transferAccelData(_arg0, _arg1, _arg2);
return true;
}
case TRANSACTION_transferNotificationData:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
this.transferNotificationData(_arg0, _arg1);
return true;
}
case TRANSACTION_transferFileData:
{
data.enforceInterface(DESCRIPTOR);
byte[] _arg0;
int _arg0_length = data.readInt();
if ((_arg0_length<0)) {
_arg0 = null;
}
else {
_arg0 = new byte[_arg0_length];
}
long _arg1;
_arg1 = data.readLong();
this.transferFileData(_arg0, _arg1);
reply.writeByteArray(_arg0);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements thealphalabs.alphaapp.IDataTransferService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
/**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
@Override public void connectTo(java.lang.String address) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(address);
mRemote.transact(Stub.TRANSACTION_connectTo, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void transferMouseData(float x, float y, int pressure) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeFloat(x);
_data.writeFloat(y);
_data.writeInt(pressure);
mRemote.transact(Stub.TRANSACTION_transferMouseData, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
@Override public void transferStringData(java.lang.String text) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(text);
mRemote.transact(Stub.TRANSACTION_transferStringData, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
@Override public void transferGyroData(float x, float y, float z) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeFloat(x);
_data.writeFloat(y);
_data.writeFloat(z);
mRemote.transact(Stub.TRANSACTION_transferGyroData, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
@Override public void transferAccelData(float x, float y, float z) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeFloat(x);
_data.writeFloat(y);
_data.writeFloat(z);
mRemote.transact(Stub.TRANSACTION_transferAccelData, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
@Override public void transferNotificationData(java.lang.String title, java.lang.String text) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(title);
_data.writeString(text);
mRemote.transact(Stub.TRANSACTION_transferNotificationData, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
@Override public void transferFileData(byte[] bytes, long file_size) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((bytes==null)) {
_data.writeInt(-1);
}
else {
_data.writeInt(bytes.length);
}
_data.writeLong(file_size);
mRemote.transact(Stub.TRANSACTION_transferFileData, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
}
static final int TRANSACTION_connectTo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_transferMouseData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_transferStringData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_transferGyroData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_transferAccelData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_transferNotificationData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_transferFileData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
}
/**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
public void connectTo(java.lang.String address) throws android.os.RemoteException;
public void transferMouseData(float x, float y, int pressure) throws android.os.RemoteException;
public void transferStringData(java.lang.String text) throws android.os.RemoteException;
public void transferGyroData(float x, float y, float z) throws android.os.RemoteException;
public void transferAccelData(float x, float y, float z) throws android.os.RemoteException;
public void transferNotificationData(java.lang.String title, java.lang.String text) throws android.os.RemoteException;
public void transferFileData(byte[] bytes, long file_size) throws android.os.RemoteException;
}
