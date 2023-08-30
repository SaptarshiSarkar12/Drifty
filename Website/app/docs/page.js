import Footer from "@/app/Footer";
import Header from "@/app/Header";
import Docs from "@/app/docs/Docs";

export default async function docs() {
    return(
        <>
           <Header props={"bg-top"}/>
           <Docs />
           <Footer />
        </>
    )
}