import remarkGfm from 'remark-gfm'
import createMDX from '@next/mdx'

/** @type {import('next').NextConfig} */
const nextConfig = {
    basePath: process.env.BASE_PATH,
    pageExtensions: ["js", "jsx", "ts", "tsx", "md", "mdx"],
    output: 'export',
    images: {
        minimumCacheTTL: 60 * 60 * 24,
        remotePatterns: [
            {
                protocol: 'https',
                hostname: 'avatars.githubusercontent.com',
            },
            {
                protocol: 'https',
                hostname: 'camo.githubusercontent.com',
            },
            {
                protocol: 'https',
                hostname: 'cdn.jsdelivr.net',
            }
        ],
        dangerouslyAllowSVG: true,
        unoptimized: true
    }
}

const withMDX = createMDX({
    extension: /\.mdx?$/,
    options: {
        remarkPlugins: [remarkGfm],
        rehypePlugins: [],
        // If you use `MDXProvider`, uncomment the following line.
        providerImportSource: "@mdx-js/react",
    },
})
export default withMDX(nextConfig)