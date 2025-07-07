import * as THREE from 'https://cdn.skypack.dev/three@0.129.0/build/three.module.js';
import { GLTFLoader } from 'https://cdn.skypack.dev/three@0.129.0/examples/jsm/loaders/GLTFLoader.js';
import { gsap } from 'https://cdn.skypack.dev/gsap';

// === SETUP: HO-OH (scene 1) ===
const camera1 = new THREE.PerspectiveCamera(80, window.innerWidth / window.innerHeight, 0.1, 1000);
camera1.position.z = 13;
const scene1 = new THREE.Scene();
const renderer1 = new THREE.WebGLRenderer({ alpha: true });
renderer1.setSize(window.innerWidth, window.innerHeight);
document.getElementById('container3d1').appendChild(renderer1.domElement);

// === SETUP: LUGIA (scene 2) ===
const camera2 = new THREE.PerspectiveCamera(80, window.innerWidth / window.innerHeight, 0.1, 1000);
camera2.position.z = 12.3;
const scene2 = new THREE.Scene();
const renderer2 = new THREE.WebGLRenderer({ alpha: true });
renderer2.setSize(window.innerWidth, window.innerHeight);
document.getElementById('container3d2').appendChild(renderer2.domElement);

// === LIGHTING ===
const addLights = (scene) => {
  const ambient = new THREE.AmbientLight(0xffffff, 0.5);
  const directional = new THREE.DirectionalLight(0xffffff, 0.5);
  directional.position.set(500, 500, 500);
  scene.add(ambient, directional);
};
const addLights2 = (scene) => {
  const ambient = new THREE.AmbientLight(0xffffff, 1);
  const directional = new THREE.DirectionalLight(0xffffff,0.3);
  directional.position.set(500, 500, 500);
  scene.add(ambient, directional);
};
addLights(scene1);
addLights2(scene2);

// === VARIABEL ===
let bee, lugia, mixer, lugiaMixer;
let currentTarget = null;
let activeSectionId = null;
let isFrozen = false;

const loader = new GLTFLoader();

// === LOAD HO-OH ===
loader.load('/model3d/ho_oh.glb', function (gltf) {
  bee = gltf.scene;
  bee.scale.set(0.02, 0.02, 0.01);
  bee.rotation.set(0, 0, 0);
  bee.position.set(-8.5, 6, 0);
  scene1.add(bee);

  mixer = new THREE.AnimationMixer(bee);
  mixer.clipAction(gltf.animations[0]).play();

  modelMove();
  smoothPosition.copy(bee.position);
});

// === LOAD LUGIA ===
loader.load('/model3d/Untitled.glb', function (gltf) {
  lugia = gltf.scene;
  lugia.scale.set(0.05, 0.045, 0.03);
  lugia.rotation.set(9, 0, -7);
  lugia.position.set(13, 1.5, -2);
  scene2.add(lugia);

   // === Siapkan Mixer tanpa memulai animasi ===
  if (gltf.animations.length > 0) {
    lugiaMixer = new THREE.AnimationMixer(lugia);
    gltf.animations.forEach((clip) => {
      const action = lugiaMixer.clipAction(clip);
      action.clampWhenFinished = true;
      action.loop = THREE.LoopRepeat;
      action.enabled = false;
      action.paused = true;
    });
  }

  modelMove2();

  lugia.traverse((child) => {
    if (child.isMesh && child.material) {
      child.material.transparent = true;
      child.material.opacity = 0;
    }
  });
});

// === POSISI BERDASARKAN SECTION ===
const arrPositionModel = [
  { id: 'herosection', position: { x: 2, y: 2, z: 0 }, rotation: { x: 0, y: 0.5, z: 0 }, scale: { x: 0.02, y: 0.02, z: 0.02 } },
  { id: 'banner2', position: { x: 10, y: 6, z: 0 }, rotation: { x: 0.389343, y: -0.411085, z: 0.624581 }, scale: { x: 0.01, y: 0.01, z: 0.01 } },
  { id: 'banner3', position: { x: 3, y: -3, z: 0 }, rotation: { x: -0.7, y: -0.3, z: 0.1 }, scale: { x: 0.02, y: 0.025, z: 0.01 } }
];
const arrPositionModel2 = [
  { id: 'banner2', position: { x: 10.5, y: -0.1, z: -2 }, rotation: { x: 3, y: 0, z: -7 }, scale: { x: 0.02, y: 0.04, z: 0.05 } },
  { id: 'banner3', position: { x: 9, y: -4, z: -2 },
  rotation: { x:3.5, y: 1.1 , z: -3.3 }, // Rotasi 90 derajat di sumbu Z
  scale: { x: 0.055, y: 0.06, z: 0.05 } }
];

// === ATUR OPACITY LUGIA ===
const setLugiaVisibility = (visible) => {
  if (!lugia) return;
  lugia.traverse((child) => {
    if (child.isMesh && child.material) {
      child.material.transparent = true;
      gsap.to(child.material, {
        opacity: visible ? 1 : 0,
        duration: 1.5,
        ease: 'power2.out'
      });
    }
  });
};

// === PENGATURAN MODEL HO-OH ===
const modelMove = () => {
  const sections = document.querySelectorAll('.section');
  let currentSection = null;

  sections.forEach((section) => {
    const rect = section.getBoundingClientRect();
    if (rect.top <= window.innerHeight / 3) {
      currentSection = section.id;
    }
  });

  activeSectionId = currentSection;

  setLugiaVisibility(currentSection !== 'herosection');

  const position_active = arrPositionModel.findIndex((val) => val.id === currentSection);
  if (position_active >= 0 && bee) {
    const new_coordinates = arrPositionModel[position_active];
    currentTarget = {
      position: { ...new_coordinates.position },
      rotation: { ...new_coordinates.rotation },
    };

    gsap.to(bee.position, { ...new_coordinates.position, duration: 2, ease: 'power1.out' });
    gsap.to(bee.rotation, { ...new_coordinates.rotation, duration: 2, ease: 'power1.out' });
    gsap.to(bee.scale, { ...new_coordinates.scale, duration: 2, ease: 'power1.out' });
  }
};

// === PENGATURAN MODEL LUGIA ===
const modelMove2 = () => {
  const sections = document.querySelectorAll('.section');
  let currentSection = null;

  sections.forEach((section) => {
    const rect = section.getBoundingClientRect();
    if (rect.top <= window.innerHeight / 3) {
      currentSection = section.id;
    }
  });

  activeSectionId = currentSection;

  setLugiaVisibility(currentSection !== 'herosection');

  const position_active2 = arrPositionModel2.findIndex((val) => val.id === currentSection);
  if (position_active2 >= 0 && lugia) {
    const new_coordinates = arrPositionModel2[position_active2];
    currentTarget = {
      position: { ...new_coordinates.position },
      rotation: { ...new_coordinates.rotation },
    };

    gsap.to(lugia.position, { ...new_coordinates.position, duration: 2, ease: 'power1.out' });
    gsap.to(lugia.rotation, { ...new_coordinates.rotation, duration: 2, ease: 'power1.out' });
    gsap.to(lugia.scale, { ...new_coordinates.scale, duration: 2, ease: 'power1.out' });

    // === Aktifkan animasi Lugia hanya di banner3 ===
    if (lugiaMixer) {
      lugiaMixer._actions.forEach(action => {
        if (currentSection === 'banner3') {
          action.enabled = true;
          action.paused = false;
           action.clampWhenFinished = true;
          action.loop = THREE.LoopOnce; 
          action.play();
          lugiaMixer.timeScale = 0.1; // Memperlambat animasi Lugia
        } else {
          action.stop();
          action.enabled = false;
          action.paused = true;
        }
      });
    }
  }
};

window.addEventListener('scroll', () => {
  if (bee) modelMove();
});
window.addEventListener('scroll', () => {
  if (lugia) modelMove2();
});

// === MOUSE FOLLOW HO-OH ===
const mouse = new THREE.Vector2();
const targetMouse3D = new THREE.Vector3();
const smoothPosition = new THREE.Vector3();

window.addEventListener('mousemove', (event) => {
  if (!bee || isFrozen || activeSectionId !== 'herosection') return;

  mouse.x = (event.clientX / window.innerWidth) * 2 - 1;
  mouse.y = -(event.clientY / window.innerHeight) * 2 + 1;

  const vector = new THREE.Vector3(mouse.x, mouse.y, 0.5);
  vector.unproject(camera1);

  const dir = vector.sub(camera1.position).normalize();
  const distance = (bee.position.z - camera1.position.z) / dir.z;
  targetMouse3D.copy(camera1.position).add(dir.multiplyScalar(distance));

  targetMouse3D.x = THREE.MathUtils.clamp(targetMouse3D.x, -10, 10);
  targetMouse3D.y = THREE.MathUtils.clamp(targetMouse3D.y, -5, 8);
});

// === KLIK UNTUK FREEZE HO-OH ===
window.addEventListener('click', () => {
  isFrozen = !isFrozen;
});

// === RENDER LOOP SCENE 1 ===
const renderScene1 = () => {
  requestAnimationFrame(renderScene1);
  renderer1.render(scene1, camera1);
  if (mixer) mixer.update(0.02);

  if (bee && activeSectionId === 'herosection' && !isFrozen) {
    smoothPosition.lerp(targetMouse3D, 0.05);
    bee.position.x = smoothPosition.x;
    bee.position.y = smoothPosition.y;
  }
};
renderScene1();

// === RENDER LOOP SCENE 2 ===
const renderScene2 = () => {
  requestAnimationFrame(renderScene2);
  renderer2.render(scene2, camera2);
  if (lugiaMixer) lugiaMixer.update(0.02);
};
renderScene2();

// === RESPONSIVE RESIZE ===
window.addEventListener('resize', () => {
  const width = window.innerWidth;
  const height = window.innerHeight;

  renderer1.setSize(width, height);
  camera1.aspect = width / height;
  camera1.updateProjectionMatrix();

  renderer2.setSize(width, height);
  camera2.aspect = width / height;
  camera2.updateProjectionMatrix();
}); 
