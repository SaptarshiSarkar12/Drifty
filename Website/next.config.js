/** @type {import('next').NextConfig} */
const nextConfig = {
    pageExtensions: ['js', 'jsx', 'md', 'mdx'],
    basePath: process.env.BASE_PATH,
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
    },
}

const withMDX = require('@next/mdx')({
    extension: /\.mdx?$/
})
module.exports = withMDX(nextConfig)