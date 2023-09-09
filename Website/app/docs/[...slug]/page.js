import fs from 'fs'
import path from "path";
import matter from 'gray-matter';
import { MDXRemote } from 'next-mdx-remote/rsc';
import DocsLayout from "@/app/docs/DocsLayout";
import remarkGfm from "remark-gfm";
import rehypeHighlight from 'rehype-highlight';
import langHttp from 'highlight.js/lib/languages/http'
import langNginx from 'highlight.js/lib/languages/nginx'
import "@/app/github-dark.css"

export async function generateStaticParams() {
    const sections = await fetch("https://raw.githubusercontent.com/SaptarshiSarkar12/Drifty/preview-docs/Website/public/sections.json").then(res => res.json())
    // const sections = JSON.parse(fs.readFileSync(path.join('public', 'sections.json'), 'utf8'))
    return sections.sections.map(page => ({
        params: {
            slug: page.filename.replace('.mdx', '').concat(page.hasChildren ? page.children.map(child => child.filename.replace('.mdx', '')) : []
            )
        }
    }));
}

function getPageContent(slug) {
    const file = fs.readFileSync(path.join('public/docs', slug + '.mdx'), 'utf8')
    const { data, content } = matter(file)
    return {
        data,
        slug,
        content
    }
}

export default function Page({ params }) {
    const page = getPageContent(params.slug)
    const options = {
        mdxOptions: {
            remarkPlugins: [remarkGfm],
            rehypePlugins: [rehypeHighlight, { languages: { http: langHttp, nginx: langNginx} }]
        }
    }
    return (
        <DocsLayout
            className={"grid md:grid-flow-col"}
        >
            <article className={"prose max-w-sm md:max-w-none prose-lg text-black prose-headings:text-gray-900"}>
                <MDXRemote source={page.content} options={options} />
            </article>
        </DocsLayout>
    )
}