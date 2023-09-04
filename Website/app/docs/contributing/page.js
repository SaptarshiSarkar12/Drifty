"use client";
import Contributing from "./contributing.mdx";
import Header from "@/app/Header";
import Footer from "@/app/Footer";

export default function contributing() {
    return(
        <>
            <Header props={"bg-top"}/>
            <Contributing />
            <Footer />
        </>
    )
}