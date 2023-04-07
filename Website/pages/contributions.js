import Contribute from "/components/Contribute";
import Header from "/components/Header";
import Footer from "/components/Footer";

export default function contributions(props) {
    return(
        <>
        <Header props={"bg-var"}/>
        <Contribute props={props.contribs}/>
        <Footer />
    </>
    )   
}  
export async function getStaticProps(){
    const res = await fetch('https://api.github.com/repos/SaptarshiSarkar12/Drifty/contributors',{method:'GET'},{next:{revalidate:3600}})
    const contrib= await res.json();
    return {
        props:{contribs:{contrib}},
        revalidate:3600
    }
  }