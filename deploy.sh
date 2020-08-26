set -ex

lein cljsbuild once min

rm -rf deploy
git clone git@github.com:remvee/tree-logo.git deploy
cd deploy
git checkout -b gh-pages --track origin/gh-pages

rm -rf -- *
mkdir css js
cp ../resources/public/index.html .
cp ../resources/public/css/style.css css/
cp -r ../resources/public/js/tree_logo.js js/

git add -- *
git commit -m '..'
git push
