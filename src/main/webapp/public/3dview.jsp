<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  

<!DOCTYPE html>
<html lang="en">
<header>
<script type="module">
import * as THREE from 'https://metaindex.fr:20000/webapp/public/commons/deps/threejs/three.module.js';

let camera, scene, renderer;
let geometry, material, mesh;

init();

function init() {

        camera = new THREE.PerspectiveCamera( 70, window.innerWidth / window.innerHeight, 0.01, 10 );
        camera.position.z = 1;

        scene = new THREE.Scene();

        geometry = new THREE.BoxGeometry( 0.2, 0.2, 0.2 );
        material = new THREE.MeshNormalMaterial();

        mesh = new THREE.Mesh( geometry, material );
        scene.add( mesh );

        renderer = new THREE.WebGLRenderer( { antialias: true } );
        renderer.setSize( window.innerWidth, window.innerHeight );
        renderer.setAnimationLoop( animation );
        document.body.appendChild( renderer.domElement );

}

function animation( time ) {

        mesh.rotation.x = time / 2000;
        mesh.rotation.y = time / 1000;

        renderer.render( scene, camera );

}
</script>
</header>
<body>

</body>
</html>
