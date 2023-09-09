import fs from 'fs'
import path from "path";
import matter from 'gray-matter';
import { MDXRemote } from 'next-mdx-remote/rsc';
import DocsLayout from "@/app/docs/DocsLayout";
import remarkGfm from "remark-gfm";
import rehypeHighlight from 'rehype-highlight';
import "@/app/github-dark.css"

export async function generateStaticParams() {
    const sections = [
        {
            title: "Quickstart",
            description: "The fastest way to get started with Drifty",
            href: "quickstart"
        },
        {
            title: "Getting Started",
            description: "Learn how to get started with the project",
            href: "getting-started"
        },
        {
            title: "FAQ",
            description: "Frequently asked questions",
            href: "faq"
        },
        {
            title: "Contributing",
            description: "Learn how to contribute to the project",
            href: "contributing"
        }
    ]
    return sections.map((page) => ({
        slug: page.href
    }));
}

function getPageContent(slug) {
    const file = fs.readFileSync(path.join('public/docs', slug + '.mdx'), 'utf8')
    const { data, content } = matter(file)
    return {
        data,
        content
    }
}

export default function Page({ params }) {
    const page = getPageContent(params.slug)
    const options = {
        mdxOptions: {
            remarkPlugins: [remarkGfm],
            rehypePlugins: [rehypeHighlight]
        }
    }
    return (
        <DocsLayout className={"grid md:grid-flow-col"}>
            <article className={"float-left prose max-w-sm xs:max-w-xs md:max-w-none prose-lg text-black prose-headings:text-gray-900"}>
                <MDXRemote source={page.content} options={options} />
            </article>
        </DocsLayout>
    )
}