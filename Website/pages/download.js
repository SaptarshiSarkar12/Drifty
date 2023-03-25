import Releases from "@/components/Releases";
import Header from "@/components/Header";
import Footer from "@/components/Footer";

export default function download(props) {
    return(
        <>
        <Header props={"bg-top"}/>
        <Releases className="bg-about" props={props.releases}/>
        <Footer />
    </>
    )    
}  
export async function getServerSideProps(){
    const res = await fetch('https://api.github.com/repos/SaptarshiSarkar12/Drifty/releases');
    const release= await res.json();
    return {
        props:{releases:{release}}
    }
  }