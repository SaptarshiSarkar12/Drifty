import Footer from "@/app/Footer";
import Header from "@/app/Header";
import DocsIndex from "@/app/docs/DocsIndex";

export const metadata = {
    title: {
        template: "Documentation - %s",
        default: "Documentation"
    },
    description: "Learn how to get started with the project",
}

export default function docs() {
    return(
        <>
            <Header props={"bg-top"}/>
            <DocsIndex />
            <Footer />
        </>
    )
}