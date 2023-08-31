import Link from "next/link";

export default function Docs() {
    const sections = [
        {
            title: "Documentation",
            description: "Documentation of the project",
            href: "/docs",
            hasChildren: false
        },
        {
            title: "Quickstart",
            description: "The fastest way to get started with Drifty",
            href: "/docs/quickstart",
            hasChildren: true,
            children: [
                {  name: "Quickstart with Drifty GUI", href: "/docs/quickstart-drifty-gui" },
                {  name: "Quickstart with Drifty CLI", href: "/docs/quickstart-drifty-cli" }
            ]
        },
        {
            title: "Getting Started",
            description: "Learn how to get started with the project",
            href: "/docs/getting-started",
            hasChildren: true
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
    return(
        <div className="text-center bg-gradient-to-b from-top to-bottom">
            <h1 className={"text-5xl p-5 pb-7"}>Documentation</h1>
            <div className={"flex flex-col md:flex-row flex-2 gap-3"}>
                <div className="flex flex-col md:flex-row flex-1 pl-3">
                    <aside className="w-auto md:w-60">
                        <nav>
                            <ul>
                                {sections.map((item) => (
                                    <li className="m-2 text-black hover:text-white" key={item.title}>
                                        <Link href={item.href}
                                            className={"flex font-bold p-2 bg-transparent rounded hover:bg-blue-600 cursor-pointer"}
                                        >
                                            {item.title}
                                            {item.hasChildren &&
                                                <button className={"p-1 fas fa-chevron-right"}></button>
                                            }
                                        </Link>
                                    </li>
                                ))}
                            </ul>
                        </nav>
                    </aside>
                </div>
                <div className={"grid grid-cols-2 gap-2 md:grid-cols-4 p-5"}>
                    {sections.map((page, index) => {
                        if (index !== 0) {
                            return (
                                <Link key={index}
                                      className={"flex flex-col flex-1 border-2 border-white bg-white rounded-lg shadow-lg p-4 hover:border-blue-500 hover:border-2"}
                                      href={"/docs/" + page.title}>
                                    <h1 className={"text-2xl "}>{page.title}</h1>
                                    <label className={"text-sm"}>{page.description}</label>
                                </Link>
                            )
                        }
                    })}
                </div>
                </div>
        </div>
    )
}