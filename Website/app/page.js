import Header from "./Header"
import MainSection from "./MainSection"
import Contribute from "./Contribute";
import Demo from "./Demo"
import Footer from "./Footer";
import Features from "@/app/features";

export default async function Home() {
    const data = await getData()
    return (
        <>
            <Header props={"bg-top"}/>
            <MainSection/>
            <Contribute props={data}/>
            <Features/>
            <Demo/>
            <Footer/>
        </>
    )
}

export async function getData(){
    const res = await fetch('https://api.github.com/repos/SaptarshiSarkar12/Drifty/contributors?per_page=100&page=1', {method:'GET'})
    const contrib = await res.json();
    return {
        contrib,
        revalidate: 3600
    }
}