// transforms markdown into html using remark

const remark = require('remark')
const html = require('remark-html')

module.exports = async function markdownToHtml(markdown) {
  const result = await remark().use(html).process(markdown)
  return result.toString()
}
