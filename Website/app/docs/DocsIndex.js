import DocsLayout from "@/app/docs/DocsLayout";
import Link from "next/link";

const sections = [
    {
        title: "Quickstart",
        description: "The fastest way to get started with Drifty",
        href: "/docs/quickstart",
        hasChildren: true,
        children: [
            {  name: "Drifty GUI", href: "/docs/quickstart-drifty-gui" },
            {  name: "Drifty CLI", href: "/docs/quickstart-drifty-cli" }
        ]
    },
    {
        title: "Getting Started",
        description: "Learn how to get started with the project",
        href: "/docs/getting-started",
        hasChildren: true,
        children: [
            {  name: "Drifty GUI", href: "/docs/getting-started-drifty-gui" },
            {  name: "Drifty CLI", href: "/docs/getting-started-drifty-cli" }
        ]
    },
    {
        title: "FAQ",
        description: "Frequently asked questions",
        href: "/docs/faq",
        hasChildren: false
    },
    {
        title: "Contributing",
        description: "Learn how to contribute to the project",
        href: "/docs/contributing",
        hasChildren: false
    },
    {
        title: "Troubleshooting",
        description: "Learn how to troubleshoot common errors",
        href: "/docs/troubleshooting",
        hasChildren: false
    }
];

export default function DocsIndex() {
    return(
        <DocsLayout>
            <div className={"grid grid-cols-2 gap-2 md:grid-cols-4 p-5"}>
                {sections.map((page, index) => {
                    return (
                        <Link key={index} className={"w-auto md:h-36 border-2 border-white bg-white rounded-lg shadow-lg p-4 hover:border-blue-600"} href={page.href}>
                            <h2 className={"text-xl md:text-2xl"}>{page.title}</h2>
                            <label className={"text-sm"}>{page.description}</label>
                        </Link>
                    )
                })}
            </div>
        </DocsLayout>
    )
}