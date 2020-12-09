package com.kanbig.faceservice.controllor;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.protobuf.ByteString;
import com.kanbig.faceservice.CompareImageRequest;
import com.kanbig.faceservice.CompareOnDbRequest;
import com.kanbig.faceservice.FaceCompareResponse;
import com.kanbig.faceservice.FaceFindResponse;
import com.kanbig.faceservice.FaceResponse;
import com.kanbig.faceservice.FeatureResponse;
import com.kanbig.faceservice.Image;
import com.kanbig.faceservice.ImageRequest;
import com.kanbig.faceservice.KanbigImageServiceGrpc;
import com.kanbig.faceservice.OpResponse;
import com.kanbig.faceservice.core.Model;
import com.kanbig.faceservice.core.Result;

@RestController
@RequestMapping("demo")
public class DemoControllor {
	private final int db = 1;
	private final int cam = 1;
	@Autowired
	KanbigImageServiceGrpc.KanbigImageServiceBlockingStub service;

	@PostMapping("enroll")
	public Result enroll(@RequestParam("files") MultipartFile files,
			@RequestParam(name = "personId", defaultValue = "") String personId) {
		List<Model> res = new ArrayList<>();
		if (files != null) {
			try {
				long t1 = System.currentTimeMillis();
				Image image = Image.newBuilder().setFilename(files.getOriginalFilename())
						.setData(ByteString.copyFrom(files.getBytes())).build();
				long t2 = System.currentTimeMillis();
				personId = StringUtils.defaultIfBlank(personId, "test");
				OpResponse data = service.enroll(ImageRequest.newBuilder().setImageChunk(image).setCamId(cam)
						.setListId(db).setPersonID(personId).build());
				long t3 = System.currentTimeMillis();
				res.add(Model.build().add("fileName", files.getOriginalFilename()).add("personId", personId)
						.add("result", data.toString()).add("t1", t1).add("t2", t2).add("t3", t3));
			} catch (Exception e) {
				return Result.error(e.getMessage());
			}

		}
		return Result.success(res);
	}

	@PostMapping("getFeature")
	public Result getFeature(@RequestParam("files") MultipartFile[] files) {
		List<Model> res = new ArrayList<>();
		if (files != null) {
			for (MultipartFile file : files) {
				try {
					long t1 = System.currentTimeMillis();
					Image image = Image.newBuilder().setFilename(file.getOriginalFilename())
							.setData(ByteString.copyFrom(file.getBytes())).build();
					long t2 = System.currentTimeMillis();
					FeatureResponse data = service.getFeature(
							ImageRequest.newBuilder().setImageChunk(image).setCamId(1).setListId(1).build());
					long t3 = System.currentTimeMillis();

					res.add(Model.build().add("fileName", file.getOriginalFilename()).add("result", data.toString())
							.add("feature", data.getFeature() == null ? null : data.getFeature().toStringUtf8())
							.add("t1", t1).add("t2", t2).add("t3", t3));
				} catch (Exception e) {
					return Result.error(e.getMessage());
				}
			}
		}
		return Result.success(res);
	}

	@PostMapping("compareOnDb")
	public Result compareOnDb(@RequestParam("files") MultipartFile files,
			@RequestParam(name = "faceId", defaultValue = "") int faceId) {
		List<Model> res = new ArrayList<>();
		if (files != null) {
			try {
				long t1 = System.currentTimeMillis();
				Image image = Image.newBuilder().setFilename(files.getOriginalFilename())
						.setData(ByteString.copyFrom(files.getBytes())).build();
				long t2 = System.currentTimeMillis();
				FaceCompareResponse data = service.compareOnDb(
						CompareOnDbRequest.newBuilder().setImg(image).setFaceId(faceId).setListId(db).build());
				long t3 = System.currentTimeMillis();
				res.add(Model.build().add("fileName", files.getOriginalFilename()).add("faceId", faceId)
						.add("result", data.toString()).add("t1", t1).add("t2", t2).add("t3", t3));
			} catch (Exception e) {
				return Result.error(e.getMessage());
			}
		}
		return Result.success(res);
	}

	@PostMapping("compareImage")
	public Result compareImage(@RequestParam("file1") MultipartFile file1, @RequestParam("file2") MultipartFile file2) {
		List<Model> res = new ArrayList<>();
		if (file1 != null && file2 != null) {
			try {
				long t1 = System.currentTimeMillis();
				Image image1 = Image.newBuilder().setFilename(file1.getOriginalFilename())
						.setData(ByteString.copyFrom(file1.getBytes())).build();
				Image image2 = Image.newBuilder().setFilename(file2.getOriginalFilename())
						.setData(ByteString.copyFrom(file2.getBytes())).build();
				long t2 = System.currentTimeMillis();
				FaceCompareResponse data = service
						.compareImage(CompareImageRequest.newBuilder().setImg(image1).setOrigin(image2).build());
				long t3 = System.currentTimeMillis();
				res.add(Model.build().add("fileName1", file1.getOriginalFilename())
						.add("fileName2", file2.getOriginalFilename()).add("result", data.toString()).add("t1", t1)
						.add("t2", t2).add("t3", t3));
			} catch (Exception e) {
				return Result.error(e.getMessage());
			}
		}
		return Result.success(res);
	}

	@PostMapping("searchFace")
	public Result searchFace(@RequestParam("files") MultipartFile files) {
		List<Model> res = new ArrayList<>();
		if (files != null) {
			try {
				long t1 = System.currentTimeMillis();
				Image image = Image.newBuilder().setFilename(files.getOriginalFilename())
						.setData(ByteString.copyFrom(files.getBytes())).build();
				long t2 = System.currentTimeMillis();
				FaceResponse data = service
						.searchFace(ImageRequest.newBuilder().setImageChunk(image).setCamId(cam).setListId(db).build());
				long t3 = System.currentTimeMillis();
				res.add(Model.build().add("fileName", files.getOriginalFilename()).add("result", data.toString())
						.add("t1", t1).add("t2", t2).add("t3", t3));
			} catch (Exception e) {
				return Result.error(e.getMessage());
			}
		}
		return Result.success(res);
	}

	@PostMapping("identifyFace")
	public Result identifyFace(@RequestParam("files") MultipartFile files,
			@RequestParam(name = "personId", defaultValue = "") String personId) {
		List<Model> res = new ArrayList<>();
		personId = StringUtils.defaultIfBlank(personId, "test");
		if (files != null) {
			try {
				long t1 = System.currentTimeMillis();
				Image image = Image.newBuilder().setFilename(files.getOriginalFilename())
						.setData(ByteString.copyFrom(files.getBytes())).build();
				long t2 = System.currentTimeMillis();
				FaceResponse data = service.identifyFace(
						ImageRequest.newBuilder().setImageChunk(image).setPersonID(personId).setCamId(cam).setListId(db).build());
				long t3 = System.currentTimeMillis();
				res.add(Model.build().add("fileName", files.getOriginalFilename()).add("result", data.toString())
						.add("t1", t1).add("t2", t2).add("t3", t3));
			} catch (Exception e) {
				return Result.error(e.getMessage());
			}
		}
		return Result.success(res);
	}
	
	
	@PostMapping("findFaces")
	public Result findFaces(@RequestParam("files") MultipartFile files) {
		List<Model> res = new ArrayList<>();
		if (files != null) {
			try {
				long t1 = System.currentTimeMillis();
				Image image = Image.newBuilder().setFilename(files.getOriginalFilename())
						.setData(ByteString.copyFrom(files.getBytes())).build();
				long t2 = System.currentTimeMillis();
				 FaceFindResponse data = service.findFaces(
						ImageRequest.newBuilder().setImageChunk(image).setCamId(cam).setListId(db).build());
				long t3 = System.currentTimeMillis();
				res.add(Model.build().add("fileName", files.getOriginalFilename()).add("result", data.toString())
						.add("t1", t1).add("t2", t2).add("t3", t3));
			} catch (Exception e) {
				return Result.error(e.getMessage());
			}
		}
		return Result.success(res);
	}
}
