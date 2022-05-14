package com.line.bot.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.line.bot.Pojos.Members;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class MemberService {

    private static final Logger logger = LoggerFactory.getLogger(MemberService.class);

	private final DatabaseReference database = FirebaseInitialize.initialize();

    public DatabaseReference startMemberService() {

		DatabaseReference member = database.child("group")
				.child("netflix")
				.child("member");
		Map<String, Members> members = new HashMap<>();
		members.put("member1", new Members("9", "john", "December 9, 2022"));
		members.put("member4", new Members("9", "Doe", "December 20, 2022"));
		member.setValueAsync(members);
		return member;
    }

	public void addNewMember(String groupName,String memberId) {

		DatabaseReference memberRef = database.child("group")
				.child(groupName)
				.child("members")
				.child(memberId);
//		Map<String, Members> members = new HashMap<>();
//		members.put(memberId, new Members(memberId,"",""));
		memberRef.setValueAsync(new Members(memberId,"",""));

	}

	public void deleteMember(String groupName, String memberId) {

		DatabaseReference memberRef = database.child("group")
				.child(groupName)
				.child("members")
				.child(memberId);
		memberRef.setValueAsync(null);
	}

	public void updateLastPay(String groupName,String memberId) {

		final String DATE_FORMAT = "E, dd-MM-yyyy HH:mm:ss";
		LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Asia/Bangkok"));
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
		String formattedTime = localDateTime.format(dateTimeFormatter);

		DatabaseReference memberRef = database.child("group")
				.child(groupName)
				.child("members")
				.child(memberId)
				.child("lastPay");
		memberRef.setValueAsync(formattedTime);

	}

	public void updateDueDate(String groupName,LocalDateTime localDateTime) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("E, dd-MM-yyyy");

		String formattedTime = localDateTime.format(dateTimeFormatter);

		DatabaseReference dueDateRef = database.child("group")
				.child(groupName)
				.child("due_date");
		dueDateRef.setValueAsync(formattedTime);

	}

	public void getAllMember(String groupName) {

		DatabaseReference memberRef = database.child("group")
				.child(groupName)
				.child("members");

		memberRef.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {

				Map<String, String> user = new HashMap<>();
				for (DataSnapshot child : dataSnapshot.getChildren()) {
					String id = child.getKey();
					String lastPay = child.child("lastPay").getValue().toString();
					user.put(id, lastPay);
				}

//				System.out.println(user);
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				System.out.println(databaseError.getCode());
			}
		});
	}

	// Return true if everyone in groupName already pay
	public Boolean checkLastPay(String groupName) {

		Map<String, Boolean> map = new HashMap<>();

		DatabaseReference memberRef = database.child("group")
				.child(groupName)
				.child("members");

		String dueDate = getDueDate(groupName);

		SimpleDateFormat dueDateFormat = new SimpleDateFormat("E, dd-MM-yyyy");
		SimpleDateFormat dateFormat = new SimpleDateFormat("E, dd-MM-yyyy HH:mm:ss");

		Thread newThread = new Thread( () ->
		memberRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				for (DataSnapshot child : dataSnapshot.getChildren()) {
					//TODO :: Implement future in memory storage
					String id = child.getKey();
					String lastPay = child.child("lastPay").getValue().toString();

					try {
						Date dateFormatted = dateFormat.parse(lastPay);
						Date dueDateFormatted = dueDateFormat.parse(dueDate);
						map.put(id, dateFormatted.compareTo(dueDateFormatted) < 0);
					} catch (ParseException e) {
						throw new RuntimeException(e);
					}
				}
			}
			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		})
		);
		newThread.start();

		try {
			Thread.sleep(2000L);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		return !map.containsValue(true);
	}

	public String getDueDate(String groupName) {

		DatabaseReference groupRef = database.child("group")
				.child(groupName)
				.child("due_date");

		final String[] dueDate = new String[1];

		Thread newThread = new Thread( () ->

		groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				String dueDateRaw = dataSnapshot.getValue().toString();
				dueDate[0] = dueDateRaw;
			}
			@Override
			public void onCancelled(DatabaseError databaseError) {
				databaseError.getCode();
			}
		})
		);
		newThread.start();
		while (dueDate[0] == null) {
			try {
				Thread.sleep(500L);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		return dueDate[0];
	}

}
