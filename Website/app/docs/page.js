import Footer from "@/app/Footer";
import Header from "@/app/Header";
import Docs from "@/app/docs/Docs";

export const metadata = {
    title: "Documentation",
    description: "Learn how to get started with the project",
}

export default async function docs() {
    return(
        <>
            <Header props={"bg-top"}/>
            <Docs />
            <Footer />
        </>
    )
}