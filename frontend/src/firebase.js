
import { initializeApp } from 'firebase/app';
import { getFirestore, collection, onSnapshot, doc, addDoc, updateDoc, deleteDoc, query, where, orderBy, getDocs } from 'firebase/firestore';
import { getAuth } from 'firebase/auth';


const firebaseConfig = {
  apiKey: "AIzaSyAm_HVcG5Dc00S5cmJl2TDUWXcwyIO0upk",
  authDomain: "news-397d5.firebaseapp.com",
  projectId: "news-397d5",
  storageBucket: "news-397d5.firebasestorage.app",
  messagingSenderId: "231306275110",
  appId: "1:231306275110:web:29ba6057a7ac2e4333f9e7",
  measurementId: "G-HZEEY5S83H"
};

const app = initializeApp(firebaseConfig);
export const db = getFirestore(app);
export const auth = getAuth(app);

export { collection, onSnapshot, doc, addDoc, updateDoc, deleteDoc, query, where, orderBy, getDocs };