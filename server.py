#!/usr/bin/env python3


# from OpenSSL import SSL
from flask import *
from pymongo import *
import datetime
from bson.json_util import dumps

client = MongoClient("mongodb+srv://admin:admin123@cluster0.ze4na.mongodb.net")
db = client['wallet']
userTable = db['user_details']
transactTable = db['transactions']
userID = ''
app = Flask(__name__)
app.secret_key = b'_5#y2L"F4Q8z\n\xec]/'
# app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///students.sqlite3'
MIN_BALANCE = 20.0

# context = SSL.Context(SSL.PROTOCOL_TLSv1_2)
# context.use_privatekey_file('server.key')
# context.use_certificate_file('server.crt')


@app.route('/login',methods=['GET','POST'])
def login():
    global userID
    if request.method == 'GET':
        return 'Not supported', 401
    ph = request.form['mobile']
    x = userTable.find_one({"_id": ph})
    if x is None:
        x = userTable.insert_one({"_id": ph, "balance": MIN_BALANCE})
    userID = ph
    return {'data': 1}, 200


@app.route('/credit',methods=['GET','POST'])
def debit():
    if request.method == 'GET':
        return 'Not supported', 401
    amt = request.form['amount']
    ph = request.form['mobile']
    x = userTable.find_one({"_id": ph})
    totalAmt = float(x['balance']) + float(amt)
    userTable.update_one({"_id": ph}, {"$set": {"balance": totalAmt}})
    transactTable.insert_one({'_id': str(datetime.datetime.now(
    ).strftime('%c')), 'type': 'Credit', 'amount': amt, 'userID': ph})
    return {'data': 1}, 200


@app.route('/debit',methods=['GET','POST'])
def credit():
    # global userID
    if request.method == 'GET':
        return 'Not supported', 401
    amt = request.form['amount']
    ph = request.form['mobile']
    x = userTable.find_one({"_id": ph})
    totalAmt = float(x['balance']) - float(amt)
    if totalAmt < MIN_BALANCE:
        return {'data': 0}, 200
    userTable.update_one({"_id": ph}, {"$set": {"balance": totalAmt}})
    transactTable.insert_one({'_id': str(datetime.datetime.now(
    ).strftime('%c')), 'type': 'Debit', 'amount': amt, 'userID': ph})
    return {'data': 1}, 200


@app.route('/currentBalance',methods=['GET','POST'])
def currBal():
    if request.method == 'GET':
        return 'Not supported', 401
    ph = request.form['mobile']
    x = userTable.find_one({"_id": ph})
    return {'data': float(x['balance'])}, 200

@app.route('/list',methods=['GET','POST'])
def listAll():
    if request.method == 'GET':
        return 'Not supported', 401
    ph = request.form['mobile']
    x = transactTable.find({"userID": ph})
    l=list(x)
    # data =[]
    # for d in x:
    #     print(d)
    #     data.append({d})
    # print(data)
    return dumps(l), 200


@app.route('/',methods=['GET','POST'])
def default():
    return 'Not supported. You need to access through the mobile app', 401


if __name__ == '__main__':
    app.run('0.0.0.0', 5678, debug=False,)# ssl_context='adhoc')
