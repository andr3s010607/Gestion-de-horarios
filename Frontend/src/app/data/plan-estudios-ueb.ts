export interface MateriaPensum {
  codigo: string;
  nombre: string;
  semestre: number;
  creditos: number;
}

const porSemestre: [string, number][] = [
  ['Matemáticas 1|4;Fundamentos de ingeniería|2;Ciencias para la vida|2;Estructuración del pensamiento 1|2;Desarrollo de sistemas de información 1|4;Principios de sistemas de información|2', 1],
  ['Matemáticas 2|4;Álgebra lineal|2;Física 1|2;Desafíos desde las ciencias para la ingeniería 1|2;Estructuración del pensamiento 2|2;Desarrollo de sistemas de información 2|4;Electiva libre 1|2', 2],
  ['Bioestadística y probabilidad|2;Física 2|2;Desafíos desde las ciencias para la ingeniería 2|2;Idiomas 1|2;Desarrollo de sistemas de información 3|4;Estructuras de datos|2;Electiva libre 2|2', 3],
  ['Territorio y sostenibilidad|2;Idiomas 2|2;Estructuras de sistemas|4;Bases de datos 1|4;Infraestructura TI 1|2;Modelización informacional|2;Proyecto núcleo 1|2', 4],
  ['Fundamentos de ecología|2;Idiomas 3|2;Ingeniería de software 1|2;Bases de datos 2|2;Infraestructura TI 2|4;Economía y finanzas|2;Electiva libre 3|2', 5],
  ['Idiomas 4|2;Ingeniería de software 2|2;Complejidad algorítmica|4;Infraestructura TI 3|2;Modelos y simulación de sistemas y de información|2;Proyecto núcleo 2|2;Formulación de proyectos|2', 6],
  ['Ingeniería de software 3|4;Lenguajes de dominio específico|2;Conceptualización de proyectos de ingeniería|2;Gestión de proyectos|2;Seguridad de la información|2;Labor social|2;Electiva de profundización en ingeniería de sistemas 1|2', 7],
  ['Política, sociedad y ciudadanía|2;Preparación para la vida laboral|2;Gestión de tecnología e innovación|4;Diseño de proyectos de ingeniería|2;Sistemas inteligentes|2;Electiva de profundización en ingeniería de sistemas 2|2;Electiva en pensamiento innovador o pensamiento emprendedor|2', 8],
  ['Trabajo de grado|12;Electiva de profundización en ingeniería de sistemas 3|2;Electiva libre 4|2', 9],
];

export const materiasPensum: MateriaPensum[] = porSemestre.flatMap(([texto, semestre]) =>
  texto.split(';').map((item, indice) => {
    const [nombre, credito] = item.split('|');
    return { codigo: `IS-${semestre}${String(indice + 1).padStart(2, '0')}`, nombre, semestre, creditos: Number(credito) };
  }),
);
