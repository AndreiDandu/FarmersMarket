import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';
import {DocumentSnapshot} from 'firebase-functions/lib/providers/firestore';

admin.initializeApp();
const db: admin.firestore.Firestore = admin.firestore();
// // Start writing Firebase Functions
// // https://firebase.google.com/docs/functions/typescript
//
export const helloWorld = functions.https.onRequest((request, response) => {
    response.send("Hello from Firebase!");
});

exports.createAd = functions.firestore
    .document('Ads/{adId}')
    .onCreate((snap, context) => {
        const ad = snap.data() || {};
        const adId = context.params.adId;

        const uid = ad.uid;
        db.collection("UsersInfo").doc(uid).get().then((doc: DocumentSnapshot) => {
            const provider = doc.data();
            Object.keys(provider.followers).map((follower: any) => {
                admin.firestore().collection(`UsersInfo`).doc(follower).get().then((followerDoc: DocumentSnapshot) => {
                    const userToNotify = followerDoc.data();

                    const message = {
                        data: {
                            id: adId,
                            title: ad.title,
                            description: ad.description,
                            uid: ad.uid,
                            user: provider.fullName,
                        },
                        token: userToNotify.token
                    };
                    console.log("Sending message to: " + followerDoc.data().fullName + "/" + followerDoc.data().token);
                    console.log(JSON.stringify(message));
                    admin.messaging().send(message).then((response) => {
                        // Response is a message ID string.
                        console.log('Successfully sent message:', response);
                    })
                        .catch((error) => {
                            console.log('Error sending message:', error);
                        });

                }).catch(error => {
                    console.log(error);
                });
            });
        }).catch(error => {
            console.log(error);
        });
    });

