import Contribute from "@/components/Contribute";
import Header from "@/components/Header";
import Footer from "@/components/Footer";

export default function contributions(props) {
    return(
        <>
        <Header props={"bg-top"}/>
        <Contribute props={props.contribs}/>
        <Footer />
    </>
    )   
}  
export async function getServerSideProps(){
    const res = await fetch('https://api.github.com/repos/SaptarshiSarkar12/Drifty/contributors',{method:'GET'})
    const contrib= await res.json();
    return {
        props:{contribs:{contrib}}
    }
  }